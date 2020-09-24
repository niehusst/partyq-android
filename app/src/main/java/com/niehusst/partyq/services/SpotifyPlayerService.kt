package com.niehusst.partyq.services

import android.content.Context
import android.widget.Toast
import com.niehusst.partyq.SpotifySharedInfo
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import timber.log.Timber

class SpotifyPlayerService(context: Context, clientId: String) {

    private var spotifyAppRemote: SpotifyAppRemote? = null

    init {
        // set auth to not show since we already authenticated to get a token
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(SpotifySharedInfo.REDIRECT_URI)
            .setAuthMethod(ConnectionParams.AuthMethod.NONE)
            .showAuthView(false)
            .build()

        SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Timber.d("Connected to Spotify!")
            }

            override fun onFailure(throwable: Throwable) {
                // Something went wrong when attempting to connect
                Timber.e("Failed to connect to Spotify:\n $throwable")
                Toast.makeText(context, "Couldn't connect to Spotify app", Toast.LENGTH_LONG).show()
            }
        })
    }
}
