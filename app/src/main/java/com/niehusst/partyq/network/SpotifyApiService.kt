package com.niehusst.partyq.network

import retrofit2.http.GET
import retrofit2.http.Query

interface SpotifyApiService {
    /**
     * Get Spotify catalog information about tracks that match a keyword string.
     *
     * @param q - The search query's keywords (and optional field filters and operators), for example "roadhouse+blues"
     * @see <a href="https://developer.spotify.com/web-api/search-item/">Search for an Item</a>
     */
    @GET("/search?type=track")
    fun searchTracks(@Query("q") q: String)
}