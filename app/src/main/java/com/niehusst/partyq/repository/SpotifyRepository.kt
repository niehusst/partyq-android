package com.niehusst.partyq.repository

import android.content.Context
import android.util.Log
import com.niehusst.partyq.services.KeyFetchService
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

object SpotifyRepository {

    // set from the Spotify developer dashboard
    private val clientId =
        KeyFetchService.getSpotifyKey()
    // unused deeplink URI. Nevertheless required for Spotify auth
    private val redirectUri = "com.niehusst.partyq://callback"

    private lateinit var spotifyAppRemote: SpotifyAppRemote

    fun getSpotifyAppRemote(): SpotifyAppRemote {
        return spotifyAppRemote
    }

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
     * @param context - a valid Android context
     * @param onConnectCallback - lambda to be called on connection success (optional)
     * @param onFailCallback - lambda to be called on connection failure (optional)
     */
    fun authenticateWithSpotfiy(
        context: Context?,
        onConnectCallback: (() -> Unit)?,
        onFailCallback: (() -> Unit)?
    ) {
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(context!!, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Timber.d("Connected to Spotify!")
                onConnectCallback?.invoke()
            }

            override fun onFailure(throwable: Throwable) {
                Timber.e("Failed to connect to Spotify:\n $throwable")
                // Something went wrong when attempting to connect
                onFailCallback?.invoke()
            }
        })
    }
}
