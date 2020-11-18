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
