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

import android.app.Activity
import android.content.Context
import com.niehusst.partyq.SpotifySharedInfo.REDIRECT_URI
import com.niehusst.partyq.SpotifySharedInfo.REQUEST_CODE
import com.niehusst.partyq.network.SpotifyAuthApi
import com.niehusst.partyq.network.models.auth.RefreshResult
import com.niehusst.partyq.network.models.auth.SwapResult
import com.niehusst.partyq.services.KeyFetchService
import com.niehusst.partyq.services.UserTypeService
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse

object SpotifyAuthRepository {

    private var api: SpotifyAuthApi? = null

    /**
     * This must be called to initialize the API endpoint access.
     * This is only called when a user is preparing to authenticate with Spotify and become a host.
     */
    fun start() {
        api = SpotifyAuthApi(
            KeyFetchService.getSpotifyId(),
            KeyFetchService.getSpotifySecret()
        )
    }

    fun stop() {
        api = null
    }

    /**
     * Send an implicit intent to a LoginActivity provided by the Spotify auth SDK so the user
     * can grant partyq permission to access their Spotify account.
     * This function is run for the side-effect of getting an API auth code from Spotify, which
     * is used to obtain an OAuth token and refresh token.
     *
     * @param handlerActivity - the Activity to handle the onActivityResult callback from LoginActivity
     */
    fun authenticateWithSpotfiy(handlerActivity: Activity) {
        val request = AuthenticationRequest.Builder(
            KeyFetchService.getSpotifyId(),
            AuthenticationResponse.Type.CODE,
            REDIRECT_URI
        ) // set scope of privileges we want to access
            .setScopes(arrayOf("streaming", "playlist-read", "app-remote-control"))
            .build()

        AuthenticationClient.openLoginActivity(handlerActivity, REQUEST_CODE, request)
    }

    /* API methods */

    suspend fun getAuthTokens(code: String): SwapResult? {
        return api?.endPoints?.swapCodeForToken(code)
    }

    suspend fun refreshAuthToken(refreshToken: String?): RefreshResult? {
        return refreshToken?.let {
            api?.endPoints?.refreshToken(refreshToken)
        }
    }
}
