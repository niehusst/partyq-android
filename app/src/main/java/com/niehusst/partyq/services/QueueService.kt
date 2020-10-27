package com.niehusst.partyq.services

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.niehusst.partyq.network.models.api.Item
import java.util.Queue
import java.util.LinkedList

object QueueService {

    val dataChangedTrigger = MutableLiveData<Any?>(null)
    private var songQueue: Queue<Item> = LinkedList()

    fun enqueueSong(item: Item, isHost: Boolean) {
        if (isHost) {
            songQueue.add(item)
            notifyDataChange()

            if (songQueue.size == 1) {
                // start playing the first song (auto play will handle the rest)
                SpotifyPlayerService.playSong(item.uri)
            }

            // notify guests of updated data
            // TODO: would a periodic batched approach be better? Even if we end up sending a lot of needless packets when data isn't updated?
            CommunicationService.sendUpdatedQueue(songQueue.toList())
        } else {
            CommunicationService.sendEnqueueRequest(item)
        }
    }

    fun dequeueSong(context: Context) {
        songQueue.poll()
        if (UserTypeService.isHost(context)) {
            // send update to all guests
            CommunicationService.sendUpdatedQueue(songQueue.toList())
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

    fun clearQueue() {
        replaceQueue(emptyList())
    }

    private fun notifyDataChange() {
        dataChangedTrigger.value = null
    }
}
