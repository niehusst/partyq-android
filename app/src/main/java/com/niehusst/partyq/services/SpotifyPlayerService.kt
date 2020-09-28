package com.niehusst.partyq.services

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.niehusst.partyq.SpotifySharedInfo
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import timber.log.Timber

object SpotifyPlayerService {

    private var spotifyAppRemote: SpotifyAppRemote? = null
    var userHasSpotifyPremium: Boolean? = null

    private val _fullyInit = MutableLiveData(false)
    val fullyInit: LiveData<Boolean> = _fullyInit

    /**
     * This function must be called before any other method in this object in order to
     * connect to the Spotify app and initialize `spotifyAppRemote` for host type users.
     * If the user is a guest, this method does nothing.
     */
    fun start(context: Context, clientId: String) {
        if (UserTypeService.isHost(context)) {
            // set auth to not show since we already authenticated to get a token
            val connectionParams = ConnectionParams.Builder(clientId)
                .setRedirectUri(SpotifySharedInfo.REDIRECT_URI)
                .showAuthView(false)
                .build()

            SpotifyAppRemote.connect( // TODO: test that this works even on devices that havent authed w/ appremote b4
                context,
                connectionParams,
                object : Connector.ConnectionListener {
                    override fun onConnected(appRemote: SpotifyAppRemote) {
                        Timber.d("Connected to Spotify!")
                        spotifyAppRemote = appRemote
                        updateUserHasSpotifyPremium()
                        startAutoPlay(context) // TODO: feels bad putting this context here. i fear it will be retained in mem too long, causing problems
                    }

                    override fun onFailure(error: Throwable) {
                        // Something went wrong when attempting to connect
                        Timber.e("Failed to connect to Spotify:\n $error")
                        Toast.makeText(
                            context,
                            "Couldn't connect to Spotify app",
                            Toast.LENGTH_LONG
                        ).show()
                        // TODO: remediation of some sort (maybe they dont have the spotify app or something)
                        /*
                        if (error is NotLoggedInException || error is UserNotAuthorizedException) {
                            // Show login button and trigger the login flow from auth library when clicked
                        } else if (error is CouldNotFindSpotifyApp) {
                            // Show button to download Spotify
                        } else {}
                         */
                    }
                }
            )
        } // else do nothing
    }

    fun startAutoPlay(context: Context) {
        // Subscribe to PlayerState
        // TODO: how to start plyaing the first song??? hold local prev song = null? and compare track on evetn callback?
        //  (currently mixing responsibilty in the queue service)
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
            val track: Track = it.track
            Timber.d("Playing ${track.name} by ${track.artist.name}")
            if (it.playbackPosition == track.duration) {
                // TODO: play next song from queue
                QueueService.dequeueSong(context)
                val nextSong = QueueService.peekQueue()
                nextSong?.run {
                    playSong(this.uri)
                }
            }
        }
        // TODO: if this doesnt work, we can rely on the spotify queue and update our queue when track changes picked up by this subscirber
        //  i think the spotify queue usage might be best, since we wont have to worry about handling autoplay while app in bg
        //  (but what if user already has songs in their spotify queue when starting the app?)
    }

    /* TODO calls should be made like this???
    playerApi.getPlayerState()
        .setResultCallback(playerState -> {
            // have fun with playerState
        })
        .setErrorCallback(throwable -> {
            // =(
        });
     */

    /**
     * Play the song specified by the given URI.
     * This method requires the calling user to be the host AND be logged into the local Spotify
     * app with a Spotify Premium account. Attempting to call this method when the user does not
     * have a premium account results in a random song being played.
     */
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

    /**
     * An async call to set the value of the field `userHasSpotifyPremium`.
     */
    private fun updateUserHasSpotifyPremium() {
        spotifyAppRemote?.userApi?.capabilities
            ?.setResultCallback {
                userHasSpotifyPremium = it.canPlayOnDemand
                _fullyInit.postValue(true)
            }
            ?.setErrorCallback {
                userHasSpotifyPremium = false
                _fullyInit.postValue(true)
            }
    }
}
