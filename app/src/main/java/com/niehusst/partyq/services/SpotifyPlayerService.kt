package com.niehusst.partyq.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.niehusst.partyq.SpotifySharedInfo
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import timber.log.Timber

object SpotifyPlayerService {

    private var spotifyAppRemote: SpotifyAppRemote? = null
    var userHasSpotifyPremium: Boolean? = null

    private val _fullyInit = MutableLiveData(false)
    val fullyInit: LiveData<Boolean> = _fullyInit

    private var trackWasStarted = false

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

            SpotifyAppRemote.connect(
                context,
                connectionParams,
                object : Connector.ConnectionListener {
                    override fun onConnected(appRemote: SpotifyAppRemote) {
                        Timber.d("Connected to Spotify!")
                        spotifyAppRemote = appRemote
                        updateUserHasSpotifyPremium()
                        startAutoPlay(context)
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

    private fun startAutoPlay(context: Context) {// TODO: i fear context will be retained in mem too long, causing problems
        // Subscribe to PlayerState
        // TODO: how to start plyaing the first song??? hold local prev song = null? and compare track on evetn callback?
        //  (currently mixing responsibilty in the queue service)
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
            setTrackWasStarted(it)
            Log.e("BIGGYCHEESE", "starting cb ${it.track?.name}")
            val isPaused = it.isPaused
            val position = it.playbackPosition
            val hasEnded = trackWasStarted && isPaused && position == 0L
            Log.e("BIGGYCHEESE", "pos $position and has ended $hasEnded")
            val songPlayingIsNotQueueHead = it.track?.uri != QueueService.peekQueue()?.uri
            if (hasEnded) {
                trackWasStarted = false
                QueueService.dequeueSong(context)
                val nextSong = QueueService.peekQueue()
                if (nextSong == null) {
                    // pause before Spotify autoplay starts a random song
                    pauseSong()
                    Log.e("BIGGYCHEESE", "pausing curr song ${it.track?.name}")
                } else {
                    playSong(nextSong.uri)
                    Log.e("BIGGYCHEESE", "plyaing next song ${nextSong.name}")
                }
            } else if(songPlayingIsNotQueueHead && !it.isPaused) {
                /* Sometimes Spotify misses the end-of-song event, or something goes wrong with
                 * playing the next song and Spotify starts playing a random song from autoplay.
                 * To remedy this, we're just going to hammer app-remote w/ the correct command
                 * until it gets it right.
                 */
                Log.e("BIGGYCHEESE", "we arent playing the right song for some reason")
                val correctCurrSong = QueueService.peekQueue()
                if (correctCurrSong == null) {
                    // pause the currently playing Spotify autoplay random song
                    pauseSong()
                    Log.e("BIGGYCHEESE", "pausing curr song ${it.track?.name}")
                } else {
                    playSong(correctCurrSong.uri)
                    Log.e("BIGGYCHEESE", "plyaing next song ${correctCurrSong.name}")
                }
            }
        }
        // TODO: if this doesnt work, we can rely on the spotify queue and update our queue when track changes picked up by this subscirber
        //  i think the spotify queue usage might be best, since we wont have to worry about handling autoplay while app in bg
        //  (but what if user already has songs in their spotify queue when starting the app?)
    }

    private fun setTrackWasStarted(playerState: PlayerState) {
        val position = playerState.playbackPosition
        val duration = playerState.track.duration
        val isPlaying = !playerState.isPaused

        if (!trackWasStarted && position > 0 && duration > 0 && isPlaying) {
            trackWasStarted = true
        }
    }

    /**
     * Play the song specified by the given URI.
     * This method requires the calling user to be the host AND be logged into the local Spotify
     * app with a Spotify Premium account. Attempting to call this method when the user does not
     * have a premium account results in a random song being played.
     */
    fun playSong(songUri: String) {
        spotifyAppRemote?.playerApi?.play(songUri)?.setErrorCallback {
            Log.e("BIGGYCHEESE","playing song died. $it")
        }
    }

    fun pauseSong() {
        spotifyAppRemote?.playerApi?.pause()
    }

    fun resumeSong() {
        spotifyAppRemote?.playerApi?.resume()
    }

    fun skipSong() {
        spotifyAppRemote?.playerApi?.skipNext() // TODO: this isnt working w/ the queue
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
     * Updates the LiveData `_fullyInit` to alert observers that they can now check if the user
     * has Spotify premium.
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
