package com.niehusst.partyq.ui.nowPlaying

import androidx.lifecycle.ViewModel
import com.niehusst.partyq.network.models.Item
import com.niehusst.partyq.services.QueueService

class NowPlayingViewModel : ViewModel() {
    var currItem: Item? = null

    fun isNewCurrItem(): Boolean {
        val newHead = QueueService.peekQueue()
        if (newHead != currItem) { // TODO: but what if the same song is twice in a row?
            currItem = newHead
            return true
        }
        return false
    }
}
