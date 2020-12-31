package com.niehusst.partyq.network

import retrofit2.http.POST

interface SpotifyAuthApiService {

    @POST("/api/token")
    fun swapCodeForToken() // TODO: data is url encoded rather than in body?

    @POST("/api/token")
    fun refreshToken()
}