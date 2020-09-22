package com.niehusst.partyq.services

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.niehusst.partyq.network.models.Item
import java.util.Queue
import java.util.LinkedList

object QueueService {

    val dataChangedTrigger = MutableLiveData<Any?>(null)
    private var songQueue: Queue<Item> = LinkedList()

    fun enqueueSong(item: Item): Boolean {
        // TODO: send update to comms. if doesnt fail, add to local queue as well. Unless host; then always add to local q
        songQueue.add(item)
        notifyDataChange()
        return true // TODO: return status of comms req
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
