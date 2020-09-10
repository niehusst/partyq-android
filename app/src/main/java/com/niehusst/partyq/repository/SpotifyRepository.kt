package com.niehusst.partyq.repository

import android.content.Context
import com.niehusst.partyq.network.SpotifyApi
import com.niehusst.partyq.services.TokenHandlerService
import com.niehusst.partyq.services.UserTypeService

object SpotifyRepository {

    private var api: SpotifyApi? = null

    /**
     * This must be called to initialize the API endpoint access.
     *
     * Precondition:
     *      requires OAuth token is set before this method is called.
     */
    fun start(ctx: Context) {
        if (UserTypeService.isHost(ctx)) {
            api = SpotifyApi(TokenHandlerService.getToken(ctx))
        }
    }

    fun playSong() {
        // TODO: play song (can && should only actually PLAY the music if user is the host)
    }

    fun searchSongs(query: String) {

    }

//    private fun connected() {
//        // Play a playlist
//        spotifyAppRemote?.playerApi?.play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL")
//
//        // Subscribe to PlayerState
//        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
//            val track: Track = it.track
//            Timber.d( "${track.name} by ${track.artist.name}")
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//        // this doesn't actually stop SPOTIFY from running an playing music
//        spotifyAppRemote?.let {
//            SpotifyAppRemote.disconnect(it)
//        }
//    }
}
