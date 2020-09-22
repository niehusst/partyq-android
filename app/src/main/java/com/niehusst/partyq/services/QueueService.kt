package com.niehusst.partyq.services

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.niehusst.partyq.network.models.Item
import java.util.*

object QueueService {

    val dataChangedTrigger = MutableLiveData<Any?>(null)
    private var songQueue: Queue<Item> = LinkedList()

    fun enqueueSong(item: Item) {
        songQueue.add(item)
        // TODO: send update to comms
        notifyDataChange()
    }

    fun dequeueSong(context: Context): Item? {
        val nextSong = songQueue.poll()
        if (UserTypeService.isHost(context)) {
            // TODO: send update to all guests via comms
        }
        notifyDataChange()
        return nextSong
    }

    fun replaceQueue(newQueue: List<Item>) {
        songQueue = LinkedList(newQueue)
        notifyDataChange()
    }

    fun getQueueItems(): List<Item> {
        return songQueue.toList()
    }

    private fun notifyDataChange() {
        dataChangedTrigger.value = null
    }
}
