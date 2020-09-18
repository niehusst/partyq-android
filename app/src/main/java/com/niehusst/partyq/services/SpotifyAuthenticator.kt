package com.niehusst.partyq.services

import android.app.Activity
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

object SpotifyAuthenticator {

    // set from the Spotify developer dashboard
    private val CLIENT_ID = KeyFetchService.getSpotifyKey()
    // unused deeplink URI. Nevertheless required for Spotify auth
    private const val REDIRECT_URI = "com.niehusst.partyq://callback"
    // can be any int. Simply for verifying response from API
    const val REQUEST_CODE = 42069

    /**
     * Send an implicit intent to a LoginActivity provided by the Spotify auth SDK so the user
     * can grant partyq permission to access their Spotify account.
     * This function is run for the side-effect of getting an API auth token from Spotify.
     *
     * @param handlerActivity - the Activity to handle the onActivityResult callback from LoginActivity
     */
    fun authenticateWithSpotfiy(handlerActivity: Activity) {
// TODO: delete if appremote is unnessesary

//        val connectionParams = ConnectionParams.Builder(clientId)
//            .setRedirectUri(redirectUri)
//            .showAuthView(true)
//            .build()
//
//        SpotifyAppRemote.connect(context!!, connectionParams, object : Connector.ConnectionListener {
//            override fun onConnected(appRemote: SpotifyAppRemote) {
//                spotifyAppRemote = appRemote
//                Timber.d("Connected to Spotify!")
//                onConnectCallback?.invoke()
//            }
//
//            override fun onFailure(throwable: Throwable) {
//                Timber.e("Failed to connect to Spotify:\n $throwable")
//                // Something went wrong when attempting to connect
//                onFailCallback?.invoke()
//            }
//        })
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
