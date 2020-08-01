package com.niehusst.partyq.services

import android.content.Context
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import timber.log.Timber

class SpotifyAuthenticationService {

    private val clientId = KeyFetchService.getSpotifyKey()
    private val redirectUri = "com.niehusst.partyq://callback"
    var spotifyAppRemote: SpotifyAppRemote? = null

    private fun authenticateWithSpotfiy(
        context: Context?,
        onConnectCallback: (() -> Unit)?,
        onFailCallback: (() -> Unit)?
    ) {
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
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
