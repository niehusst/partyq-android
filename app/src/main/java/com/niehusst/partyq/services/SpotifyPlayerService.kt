/*
 * Copyright 2020 Liam Niehus-Staab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.niehusst.partyq.services

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.niehusst.partyq.BundleNames
import com.niehusst.partyq.R
import com.niehusst.partyq.SpotifySharedInfo
import com.niehusst.partyq.network.models.api.Item
import com.niehusst.partyq.ui.remediation.RemediationActivity
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp
import com.spotify.android.appremote.api.error.NotLoggedInException
import com.spotify.android.appremote.api.error.SpotifyConnectionTerminatedException
import com.spotify.android.appremote.api.error.UserNotAuthorizedException
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
                        Timber.i("Connected to Spotify!")
                        spotifyAppRemote = appRemote
                        updateUserHasSpotifyPremium()
                        startAutoPlay(context)
                    }

                    override fun onFailure(error: Throwable) {
                        // Something went wrong when attempting to connect to Spotify App
                        Timber.e("Failed to connect to Spotify:\n $error")

                        // go to RemediationActivity
                        val intent = Intent(context, RemediationActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra(
                            BundleNames.REMEDIATION_MESSAGE,

                            when (error) {
                                is NotLoggedInException,
                                is UserNotAuthorizedException -> context.resources.getString(R.string.no_spotify_premium_msg)
                                is CouldNotFindSpotifyApp -> context.resources.getString(R.string.no_spotify_msg)
                                is SpotifyConnectionTerminatedException -> context.resources.getString(R.string.spotify_crash_msg)
                                else -> context.resources.getString(R.string.generic_error_msg)
                            }
                        )
                        context.startActivity(intent)
                        // finish PartyActivity
                        (context as Activity).finish()
                    }
                }
            )
        } // else do nothing
    }

    /**
     * Automatically play the next from QueueService when each song ends, stopping playback
     * when the queue is empty.
     * This has been designed to work with (aka combat) enabled user autoplay, though a random
     * Spotify autoplay song may play for second while trying to switch to the next song that
     * should be played from QueueService.
     */
    private fun startAutoPlay(context: Context) {
        // catch PlayerState events so we can play songs from our queue back-to-back
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
            setTrackWasStarted(it)

            val isPaused = it.isPaused
            val position = it.playbackPosition
            val hasEnded = trackWasStarted && isPaused && position == 0L
            val songPlayingIsNotQueueHead = it.track?.uri != QueueService.peekQueue()?.uri

            if (hasEnded) {
                trackWasStarted = false
                QueueService.dequeueSong(context)
                val nextSong = QueueService.peekQueue()
                Timber.d("About to play ${nextSong?.name}")

                if (nextSong == null) {
                    // pause before Spotify autoplay starts a random song
                    pauseSong()
                } else {
                    playSong(nextSong.uri)
                }

                // new song has started, so old skip votes are nullified
                SkipSongHandler.clearSkipCount()
            } else if (songPlayingIsNotQueueHead && !it.isPaused) {
                /* Sometimes Spotify misses the end-of-song event, or something goes wrong with
                 * playing the next song and Spotify starts playing a random song from autoplay.
                 * To remedy this, we're just going to hammer app-remote w/ the correct command
                 * until it gets it right.
                 */
                val correctCurrSong = QueueService.peekQueue()
                if (correctCurrSong == null) {
                    // pause the currently playing Spotify autoplay random song
                    pauseSong()
                } else {
                    playSong(correctCurrSong.uri)
                }
            }
        }
    }

    private fun setTrackWasStarted(playerState: PlayerState) {
        val position = playerState.playbackPosition
        val duration = playerState.track.duration
        val isPlaying = !playerState.isPaused

        if (!trackWasStarted && position > 0 && duration > 0 && isPlaying) {
            trackWasStarted = true
        }
    }
    
    private fun ensureSongIsPlaying(currSong: Item?) {
        // ensure that currSong is playing, if not null
        spotifyAppRemote?.playerApi?.playerState?.setResultCallback { state ->
            if (state.track == null) {
                currSong?.let { song ->
                    Timber.e("Current song: \"${song.name}\" was not playing; starting it now")
                    playSong(song.uri)
                }
            }
        }
    }

    /**
     * Play the song specified by the given URI.
     * This method requires the calling user to be the host AND be logged into the local Spotify
     * app with a Spotify Premium account. Attempting to call this method when the user does not
     * have a premium account results in a random song being played rather than the one indicated
     * by `songUri`.
     */
    fun playSong(songUri: String) {
        spotifyAppRemote?.playerApi?.play(songUri)
    }

    fun pauseSong() {
        spotifyAppRemote?.playerApi?.pause()
    }

    fun resumeSong() {
        spotifyAppRemote?.playerApi?.resume()

        // enables user-initiated recovery from Spotify failing to play a song
        ensureSongIsPlaying(QueueService.peekQueue())
    }

    fun skipSong() {
        spotifyAppRemote?.playerApi?.skipNext()
    }

    /**
     * Disconnect from the remote connection to the Spotify app to prevent memory leaks.
     * Also clear app remote reference from memory to prevent reconnection.
     */
    fun disconnect() {
        // disconnecting doesn't actually stop the Spotify app from running and playing music
        spotifyAppRemote?.let { appRemote ->
            // pause the song first so music doesn't keep playing after disconnect
            appRemote.playerApi.pause().setResultCallback {
                SpotifyAppRemote.disconnect(appRemote)
            }.setErrorCallback {
                Timber.e("Error pausing Spotify:\n$it")
                // we have to disconnect anyway
                SpotifyAppRemote.disconnect(appRemote)
            }
        }

        spotifyAppRemote = null
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
