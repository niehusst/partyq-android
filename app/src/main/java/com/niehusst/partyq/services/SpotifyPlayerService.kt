package com.niehusst.partyq.services

import android.content.Context
import android.widget.Toast
import com.niehusst.partyq.SpotifySharedInfo
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import timber.log.Timber

object SpotifyPlayerService {

    private var spotifyAppRemote: SpotifyAppRemote? = null

    /**
     * This function must be called before any other method in this object in order to
     * connect to the Spotify app and initialize `spotifyAppRemote`.
     */
    fun start(context: Context, clientId: String) {
        // set auth to not show since we already authenticated to get a token
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(SpotifySharedInfo.REDIRECT_URI)
            .setAuthMethod(ConnectionParams.AuthMethod.NONE)
            .showAuthView(false)
            .build()

        SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                Timber.d("Connected to Spotify!")
                spotifyAppRemote = appRemote
                startAutoPlay()
            }

            override fun onFailure(throwable: Throwable) {
                // Something went wrong when attempting to connect
                Timber.e("Failed to connect to Spotify:\n $throwable")
                Toast.makeText(context, "Couldn't connect to Spotify app", Toast.LENGTH_LONG).show()
                // TODO: remediation of some sort (maybe they dont have the spotify app or something)
            }
        })
    }

    fun startAutoPlay() {
        // Subscribe to PlayerState
        // TODO: how to start plyaing the first song??? hold local prev song = null? and compare track on evetn callback?
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
            val track: Track = it.track
            Timber.d("Playing ${track.name} by ${track.artist.name}")
            if (it.playbackPosition == track.duration) {
                // TODO: play next song from queue
                
            }
        }
        // TODO: if this doesnt work, we can rely on the spotify queue and update our queue when track changes picked up by this subscirber
    }

    fun playSong(songUri: String) {
        spotifyAppRemote?.playerApi?.play(songUri)
    }

    fun pauseSong() {
        spotifyAppRemote?.playerApi?.pause()
    }

    fun resumeSong() {
        spotifyAppRemote?.playerApi?.resume()
    }

    fun skipSong() {
        spotifyAppRemote?.playerApi?.skipNext()
    }

    /**
     * Disconnect from the remote connection to the Spotify app to prevent memory leaks.
     * Should be executed onStop().
     */
    fun disconnect() {
        // this doesn't actually stop SPOTIFY from running and playing music
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }
}
