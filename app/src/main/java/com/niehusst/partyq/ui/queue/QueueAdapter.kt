package com.niehusst.partyq.ui.queue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.niehusst.partyq.databinding.QueueListHeaderBinding
import com.niehusst.partyq.databinding.QueueListItemBinding
import com.niehusst.partyq.network.models.Artist
import com.niehusst.partyq.network.models.Item

class QueueAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Copy of the same data in QueueService. Duplicated for stability during binding
    var queueCopy = listOf<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) {
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

    interface QueueViewHolder {
        fun bind(item: Item)

        fun artistsToPrettyString(artists: List<Artist>?): String {
            var nameList = ""
            var i = 0
            artists?.forEach { art ->
                nameList += art.name
                if (i < artists.size-1) {
                    nameList += ", "
                }
                i++
            }
            return nameList
        }
    }

    inner class QueueItemViewHolder(
        private val binding: QueueListItemBinding
    ) : QueueViewHolder, RecyclerView.ViewHolder(binding.root) {
        override fun bind(item: Item) {
            binding.songTitle.text = item.name
            binding.artistName.text = artistsToPrettyString(item.artists)
            Glide.with(binding.root)
                .load(item.album.images?.lastOrNull()?.url)
                .fitCenter()
                .into(binding.thumbnail)
        }
    }

    inner class QueueHeaderViewHolder(
        private val binding: QueueListHeaderBinding
    ) : QueueViewHolder, RecyclerView.ViewHolder(binding.root) {
        override fun bind(item: Item) {
            binding.songTitle.text = item.name
            binding.artistName.text = artistsToPrettyString(item.artists)
            Glide.with(binding.root)
                .load(item.album.images?.lastOrNull()?.url)
                .fitCenter()
                .into(binding.thumbnail)
        }
    }
}
