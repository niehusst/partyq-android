package com.niehusst.partyq.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.SearchResultItemBinding
import com.niehusst.partyq.network.models.api.Item
import com.niehusst.partyq.services.QueueService
import com.niehusst.partyq.services.UserTypeService

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ResultViewHolder>() {

    var searchResults = listOf<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SearchResultItemBinding.inflate(layoutInflater, parent, false)
        return ResultViewHolder(binding, UserTypeService.isHost(parent.context))
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(searchResults[position])
    }

    inner class ResultViewHolder(
        private val binding: SearchResultItemBinding,
        private val isHost: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.songTitle.text = item.name
            binding.artistName.text = item.artistsAsPrettyString()
            Glide.with(binding.root)
                .load(item.album.images?.lastOrNull()?.url)
                .error(R.drawable.album)
                .fitCenter()
                .into(binding.thumbnail)

            binding.root.setOnClickListener {
                // enqueue a copy so that the same exact obj ref doesnt reappear in queue
                QueueService.enqueueSong(item.copy(), isHost)
                // no good way to get result of op back w/o global state, so just show success
                showSuccess()
            }
        }

        private fun showSuccess() {
            val snackPopup = Snackbar.make(
                binding.root,
                R.string.song_added_to_queue,
                Snackbar.LENGTH_SHORT
            )
            snackPopup.view.setBackgroundColor(binding.root.context.getColor(R.color.colorSuccess))
            snackPopup.setTextColor(binding.root.context.getColor(R.color.onColorSuccess))
            snackPopup.show()
        }
    }
}
