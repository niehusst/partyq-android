package com.niehusst.partyq.services

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.niehusst.partyq.network.models.Item
import java.util.Queue
import java.util.LinkedList

object QueueService {

    val dataChangedTrigger = MutableLiveData<Any?>(null)
    private var songQueue: Queue<Item> = LinkedList()

    fun enqueueSong(item: Item, isHost: Boolean): Boolean {
        if (isHost) {
            songQueue.add(item)
            notifyDataChange()

            if (songQueue.size == 1) { // TODO: this is a bit messy?
                // start playing the first song (auto play will handle the rest)
                SpotifyPlayerService.playSong(item.uri)
            }
        } else {
            // TODO: send update to comms. if doesnt fail, add to local queue as well.
        }
        return true // TODO: return status of comms req
    }

    fun dequeueSong(context: Context) {
        songQueue.poll()
        if (UserTypeService.isHost(context)) {
            // TODO: send update to all guests via comms (is this necessary? should a whole new copy of q be sent instead? is this a host only method?)
        }
        notifyDataChange()
    }

    fun replaceQueue(newQueue: List<Item>) {
        songQueue = LinkedList(newQueue)
        notifyDataChange()
    }

    fun getQueueItems(): List<Item> {
        return songQueue.toList()
    }

    fun peekQueue(): Item? {
        return songQueue.peek()
    }

    private fun notifyDataChange() {
        dataChangedTrigger.value = null
    }
}
