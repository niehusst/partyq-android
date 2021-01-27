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

import timber.log.Timber

object SkipSongHandler {

    private var skipCount = 0

    /**
     * Register a single vote to skip the currently playing song. The song will only be skipped
     * if more than 50% of party goers have voted to skip since the last call to `clearSkipCount()`
     */
    fun voteSkip() {
        // +1 to include the host in the count
        val numPartyGoers = CommunicationService.connectionEndpointIds.size + 1
        Timber.e("BIGGYCHEESE skip vote registering")
        skipCount++
        if (skipCount > numPartyGoers / 2) {
            Timber.e("BIGGYCHEESE skipping song")
            // skip song
            SpotifyPlayerService.skipSong {
                // try playing queue head if skip fails
                Timber.e("BIGGYCHEESE attempting to play song on skip fail")
                QueueService.peekQueue()?.also { song ->
                    SpotifyPlayerService.playSong(song.uri)
                }
            }

            // clear registered votes for next song
            clearSkipCount()
        }
    }

    fun clearSkipCount() {
        Timber.e("BIGGYCHEESE reseting count $skipCount")
        skipCount = 0
        Timber.e("BIGGYCHEESE count now $skipCount")
    }
}
