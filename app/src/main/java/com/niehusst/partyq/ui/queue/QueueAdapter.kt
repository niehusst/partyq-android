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

package com.niehusst.partyq.ui.queue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.QueueListHeaderBinding
import com.niehusst.partyq.databinding.QueueListItemBinding
import com.niehusst.partyq.network.models.api.Item
import com.niehusst.partyq.services.QueueService

class QueueAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Copy of the same data in QueueService. Duplicated for stability during binding
    var queueCopy = QueueService.getQueueItems()

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_HEADER
        } else {
            TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            QueueHeaderViewHolder(
                QueueListHeaderBinding.inflate(layoutInflater, parent, false)
            )
        } else {
            QueueItemViewHolder(
                QueueListItemBinding.inflate(layoutInflater, parent, false)
            )
        }
    }

    override fun getItemCount() = queueCopy.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is QueueHeaderViewHolder) {
            holder.bind(queueCopy[position])
        } else {
            (holder as QueueItemViewHolder).bind(queueCopy[position])
        }
    }

    inner class QueueItemViewHolder(
        private val binding: QueueListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
         fun bind(item: Item) {
             binding.songTitle.text = item.name
             binding.artistName.text = item.artistsAsPrettyString()
             Glide.with(binding.root)
                 .load(item.album.images?.lastOrNull()?.url)
                 .error(R.drawable.album)
                 .fitCenter()
                 .into(binding.thumbnail)
        }
    }

    inner class QueueHeaderViewHolder(
        private val binding: QueueListHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
         fun bind(item: Item) {
            binding.songTitle.text = item.name
            binding.artistName.text = item.artistsAsPrettyString()
            Glide.with(binding.root)
                .load(item.album.images?.lastOrNull()?.url)
                .error(R.drawable.album)
                .fitCenter()
                .into(binding.thumbnail)
        }
    }

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }
}
