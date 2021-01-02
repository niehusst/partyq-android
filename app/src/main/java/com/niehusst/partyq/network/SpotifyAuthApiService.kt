package com.niehusst.partyq.network

import com.niehusst.partyq.SpotifySharedInfo
import com.niehusst.partyq.network.models.auth.RefreshResult
import com.niehusst.partyq.network.models.auth.SwapResult
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SpotifyAuthApiService {

    @FormUrlEncoded
    @POST("/api/token")
    suspend fun swapCodeForToken(
        @Field("code") code: String,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("redirect_uri") redirectUri: String = SpotifySharedInfo.REDIRECT_URI
    ): SwapResult

    @FormUrlEncoded
    @POST("/api/token")
    suspend fun refreshToken(
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): RefreshResult
}
