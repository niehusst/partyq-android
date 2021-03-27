/*
 * Copyright 2020 Liam Niehus-Staab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.niehusst.partyq.repository

import com.niehusst.partyq.network.SpotifyApi
import com.niehusst.partyq.network.Status
import com.niehusst.partyq.network.models.api.SearchResult
import com.niehusst.partyq.services.CommunicationService
import com.niehusst.partyq.services.SearchResultHandler
import com.niehusst.partyq.services.TokenHandlerService
import com.niehusst.partyq.utility.CrashlyticsHelper
import timber.log.Timber
import java.util.concurrent.TimeUnit

object SpotifyRepository {

    private var api: SpotifyApi? = null

    /**
     * This must be called to initialize the API endpoint access.
     *
     * Precondition:
     *      requires OAuth token is set in TokenHandlerService before this method is called.
     */
    fun start(isHost: Boolean) {
        if (isHost) {
            api = SpotifyApi(TokenHandlerService.getAuthToken())
        }
    }

    fun stop() {
        api = null
    }

    /**
     * If the user is the host, make an API call to Spotify. Otherwise, send the request to the
     * host to execute w/ their OAuth token. This method is called for the side-effect of loading
     * a SearchResult into the SearchResultHandler.
     * The management of loading state is left to the caller.
     *
     * @param query - If `isPaged` is true, `query` should be a human readable string to search
     *                against the Spotify tracks database.
     *                Else, `query` should be a valid URL for a previous/next page of a previous
     *                query of the Spotify API.
     * @param isHost - Whether or not the user is the host
     * @param isPaged - Should be true when `query` is a URL, false when `query` is a human readable string.
     *                  Dictates what type of operation is performed on `query`.
     */
    suspend fun searchSongsForLocalResult(query: String, isHost: Boolean, isPaged: Boolean) {
        if (isHost) {
            try {
                val result = if (isPaged) {
                    getPagedSearchTrackResults(query)
                } else {
                    getSearchTrackResults(query)
                } ?: throw Exception("Uninitialized api")

                // put search results where UI can find them
                SearchResultHandler.updateSearchResults(result)
                SearchResultHandler.setStatus(Status.SUCCESS)
            } catch (err: Throwable) {
                Timber.e(err)
                SearchResultHandler.setStatus(Status.ERROR)
            }
        } else {
            // the Nearby Connections callbacks (in CommunicationService) will redirect the
            // results to SearchResultsHandler for us
            CommunicationService.sendQuery(query)
        }
    }

    /**
     * Executes a GET request on the provided URL `reqUrl`.
     * Intended to perform a search of Spotify tracks for a previous/following page of results.
     *
     * @param reqUrl - URL to perform the GET request on
     * @return SearchResult - POJO built from Spotify API response. Null when `api` uninitialized.
     * @throws java.net.UnknownHostException - when calling device has no network connection
     */
    suspend fun getPagedSearchTrackResults(reqUrl: String): SearchResult? {
        ensureTokenValidity()
        return api?.endPoints?.getSearchResultPage(reqUrl)
    }

    /**
     * Perform a search of Spotify tracks that match `query` and return the results.
     *
     * @param query - Query string to search in Spotify web API
     * @return SearchResult - POJO built from Spotify API response. Null when `api` uninitialized.
     * @throws java.net.UnknownHostException - when calling device has no network connection
     */
    suspend fun getSearchTrackResults(query: String): SearchResult? {
        ensureTokenValidity()
        return api?.endPoints?.searchTracks(query, "track")
    }

    /**
     * Make a call to the Spotify auth API to get a new OAuth token for the Spotify API if the
     * previous token was expired.
     * This function is run for the side-effect of getting and saving a refreshed OAuth token into
     * `TokenHandlerService` when the previous token was expired to avoid 401 errors.
     */
    private suspend fun ensureTokenValidity() {
        if (TokenHandlerService.tokenIsExpired()) {
            val refreshed = SpotifyAuthRepository.refreshAuthToken(
                TokenHandlerService.getRefreshToken()
            )

            if (refreshed != null) {
                // save refreshed OAuth token
                TokenHandlerService.resetAuthToken(
                    refreshed.accessToken,
                    refreshed.secondsUntilExpiration,
                    TimeUnit.SECONDS
                )

                // restart api repo with new refreshed token
                start(true)
            } else {
                Timber.e("Token refresh failed")
            }
        } // else no-op; token was not yet expired
    }
}
