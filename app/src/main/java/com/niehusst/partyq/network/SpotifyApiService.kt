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

package com.niehusst.partyq.network

import com.niehusst.partyq.network.models.api.SearchResult
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface SpotifyApiService {
    /**
     * Get Spotify catalog information about tracks that match a keyword string.
     *
     * @param q - The search query's keywords (and optional field filters and operators), for example "roadhouse+blues"
     * @param type - The type of content to search for (track, artist, album). Must follow q in the URI
     * @see <a href="https://developer.spotify.com/web-api/search-item/">Search for an Item</a>
     */
    @GET("/v1/search")
    suspend fun searchTracks(
        @Query("q") q: String,
        @Query("type") type: String
    ): SearchResult

    /**
     * Perform a GET request on the provided URL.
     * The purpose of this function is to be able to easily perform a repeated request but for
     * a previous or following page of results. (Spotify search endpoint provides full URLs for
     * getting the next/prev page of results)
     *
     * @param url - The URL to perform the GET request on
     */
    @GET
    suspend fun getSearchResultPage(
        @Url url: String
    ): SearchResult
}
