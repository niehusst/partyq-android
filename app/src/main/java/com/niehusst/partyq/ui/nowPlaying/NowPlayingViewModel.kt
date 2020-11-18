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

package com.niehusst.partyq.ui.nowPlaying

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.niehusst.partyq.R
import com.niehusst.partyq.network.models.api.Item
import com.niehusst.partyq.services.*

class NowPlayingViewModel : ViewModel() {
    var currItem: Item? = null
    var hasVotedSkip = false

    fun isNewCurrItem(): Boolean {
        val newHead = QueueService.peekQueue()
        if (System.identityHashCode(newHead) != System.identityHashCode(currItem)) {
            currItem = newHead
            hasVotedSkip = false
            return true
        }
        return false
    }

    fun playSong() = SpotifyPlayerService.resumeSong()

    fun pauseSong() = SpotifyPlayerService.pauseSong()

    /**
     * Submit a vote to skip the current song. For the song to be skipped, more than 50% of
     * party goers must vote to skip the same song while it is being played.
     */
    fun voteSkipSong(context: Context) {
        if (hasVotedSkip) return
        hasVotedSkip = true

        if (UserTypeService.isHost(context)) {
            SkipSongHandler.voteSkip()
        } else {
            CommunicationService.sendSkipVote()
        }
        Toast.makeText(context, R.string.skip_msg, Toast.LENGTH_SHORT).show()
    }
}
