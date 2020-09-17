package com.niehusst.partyq.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.MutableLiveData
import com.niehusst.partyq.network.Resource
import com.niehusst.partyq.network.SpotifyApi
import com.niehusst.partyq.network.models.SearchResult
import com.niehusst.partyq.services.CommunicationService
import com.niehusst.partyq.services.TokenHandlerService
import com.niehusst.partyq.services.UserTypeService
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import java.lang.Exception

object SpotifyRepository {

    // TODO: do i need some way to get this back on process death recreation?
    //  (this may already be handled since onCreate in PartyActivity is called again)
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
        // TODO: play song (can this be done w/o appremote?)
    }

    /**
     * If the user is the host, make an API call to Spotify. Otherwise, send the request to the
     * host to execute. The management of loading state is left to the calling ViewModel.
     */
    suspend fun searchSongs(query: String, context: Context): Resource<SearchResult> {
        return if (UserTypeService.isHost(context)) {
            try {
                val result = api?.endPoints?.searchTracks(query, "track") ?: throw Exception("Uninitialized api")
                Resource.success(result)
            } catch (err: Throwable) {
                Timber.e(err)
                Resource.error(null, "Network error")
            }
        } else {
//            CommunicationService.sendSearchRequest(query)
            Resource.error(null, "not yet implemented")
        }
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
