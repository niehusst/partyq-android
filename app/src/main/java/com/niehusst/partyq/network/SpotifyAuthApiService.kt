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
    suspend fun refreshAuthToken(
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): RefreshResult
}
