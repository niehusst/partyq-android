package com.niehusst.partyq.repository

import android.content.Context
import com.niehusst.partyq.network.SpotifyApi
import com.niehusst.partyq.network.Status
import com.niehusst.partyq.network.models.api.SearchResult
import com.niehusst.partyq.services.CommunicationService
import com.niehusst.partyq.services.SearchResultHandler
import com.niehusst.partyq.services.TokenHandlerService
import com.niehusst.partyq.services.UserTypeService
import timber.log.Timber

object SpotifyRepository {

    private var api: SpotifyApi? = null

    /**
     * This must be called to initialize the API endpoint access.
     *
     * Precondition:
     *      requires OAuth token is set before this method is called.
     */
    fun start(ctx: Context) {
        if (UserTypeService.isHost(ctx)) {
            api = SpotifyApi(TokenHandlerService.getToken(ctx))
        }
    }

    /**
     * If the user is the host, make an API call to Spotify. Otherwise, send the request to the
     * host to execute. The management of loading state is left to the calling ViewModel.
     */
    suspend fun searchSongsForLocalResult(query: String, context: Context) {
        if (UserTypeService.isHost(context)) {
            try {
                val result = getSearchTrackResults(query) ?: throw Exception("Uninitialized api")
                SearchResultHandler.updateSearchResults(result)
                SearchResultHandler.setStatus(Status.SUCCESS)
            } catch (err: Throwable) {
                Timber.e(err)
                SearchResultHandler.setStatus(Status.ERROR)
            }
        } else {
            // the Nearby Connections callbacks will redirect the results to SearchResultsHandler
            // for us
            CommunicationService.sendQuery(query)
        }
    }

    suspend fun getSearchTrackResults(query: String): SearchResult? {
        return api?.endPoints?.searchTracks(query, "track")
    }
}
