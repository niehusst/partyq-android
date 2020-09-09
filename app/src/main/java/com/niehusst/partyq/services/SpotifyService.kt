package com.niehusst.partyq.services

object SpotifyService {
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
