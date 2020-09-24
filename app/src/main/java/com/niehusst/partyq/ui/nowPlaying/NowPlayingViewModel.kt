package com.niehusst.partyq.ui.nowPlaying

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.niehusst.partyq.network.models.Item
import com.niehusst.partyq.services.QueueService
import com.niehusst.partyq.services.UserTypeService

class NowPlayingViewModel : ViewModel() {
    var currItem: Item? = null
    var hasVotedSkip = false

    fun isNewCurrItem(): Boolean {
        val newHead = QueueService.peekQueue()
        if (newHead != currItem) { // TODO: but what if the same song is twice in a row?
            currItem = newHead
            hasVotedSkip = false
            return true
        }
        return false
    }

    fun playSong() {
        // TODO: call spotify player service
    }

    fun pauseSong() {
        // TODO: call spotfy player service
    }

    /**
     * Submit a vote to skip the current song. For the song to be skipped, more than 50% of
     * party goers must vote to skip the same song while it is being played.
     */
    fun voteSkipSong(context: Context) {
        if (hasVotedSkip) return
        hasVotedSkip = true

        if (UserTypeService.isHost(context)) {
            // TODO: add a vote towards skip song to master skip count holder
            QueueService.dequeueSong(context) // TODO: delete/delegate
        } else {
            // TODO: send vote skip req through comms
        }
        Toast.makeText(context, "Your vote to skip has been counted", Toast.LENGTH_SHORT).show()
    }
}
