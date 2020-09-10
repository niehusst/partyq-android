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

    private lateinit var spotifyAppRemote: SpotifyAppRemote

    fun getSpotifyAppRemote(): SpotifyAppRemote {
        return spotifyAppRemote
    }

    //TODO: are these docs wrong?
    /**
     * Send a request to the Spotify app (must be downloaded on same device as partyq is
     * being run on) to connect. You must also have authenticated IN the Spotify app
     * at sometime before (w/ the last 30 days?) this method is called for it to succeed.
     * If it is the first time for the calling device to authenticate with Spotify, Spotify
     * will throw up a confirmation screen that you want partyq to have access to your account
     * data.
     * This function is run for the side-effect of initializing {spotifyAppRemote}, which
     * handles all communication with the Spotify app.
     *
     * @param handlerActivity - the Activity to handle the onActivityResult callback from LoginActivity
     */
    fun authenticateWithSpotfiy(handlerActivity: Activity) {
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
