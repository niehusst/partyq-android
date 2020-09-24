package com.niehusst.partyq.services

import android.app.Activity
import com.niehusst.partyq.SpotifySharedInfo.REDIRECT_URI
import com.niehusst.partyq.SpotifySharedInfo.REQUEST_CODE
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

object SpotifyAuthenticator {

    // set from the Spotify developer dashboard
    private val CLIENT_ID = KeyFetchService.getSpotifyKey()

    /**
     * Send an implicit intent to a LoginActivity provided by the Spotify auth SDK so the user
     * can grant partyq permission to access their Spotify account.
     * This function is run for the side-effect of getting an API auth token from Spotify.
     *
     * @param handlerActivity - the Activity to handle the onActivityResult callback from LoginActivity
     */
    fun authenticateWithSpotfiy(handlerActivity: Activity) {
        val request = AuthenticationRequest.Builder(
            CLIENT_ID,
            AuthenticationResponse.Type.TOKEN,
            REDIRECT_URI
        )
            .setScopes(arrayOf("streaming", "playlist-read")) // privileges we want access to
            .build()

        AuthenticationClient.openLoginActivity(handlerActivity, REQUEST_CODE, request)
    }
}
