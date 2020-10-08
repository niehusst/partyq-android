package com.niehusst.partyq.network

import com.niehusst.partyq.network.models.api.SearchResult
import retrofit2.http.GET
import retrofit2.http.Query

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
}
