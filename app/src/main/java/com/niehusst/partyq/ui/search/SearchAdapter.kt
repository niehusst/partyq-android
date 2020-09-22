package com.niehusst.partyq.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.SearchResultItemBinding
import com.niehusst.partyq.network.models.Artist
import com.niehusst.partyq.network.models.Item
import com.niehusst.partyq.services.QueueService

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ResultViewHolder>() {

    var searchResults = listOf<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SearchResultItemBinding.inflate(layoutInflater, parent, false)
        return ResultViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(searchResults[position])
    }

    inner class ResultViewHolder(
        private val binding: SearchResultItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.songTitle.text = item.name
            binding.artistName.text = artistsToPrettyString(item.artists)
            Glide.with(binding.root)
                .load(item.album.images?.lastOrNull()?.url)
                .fitCenter()
                .into(binding.thumbnail)

            binding.root.setOnClickListener {
                // give more visual confirmation that song was added to queue
                if (QueueService.enqueueSong(item)) {
                    showSuccess()
                } else {
                    showError()
                }
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

        private fun showError() {
            val snackPopup = Snackbar.make(
                binding.root,
                R.string.error_adding_song_to_queue,
                Snackbar.LENGTH_SHORT
            )
            snackPopup.view.setBackgroundColor(binding.root.context.getColor(R.color.colorError))
            snackPopup.setTextColor(binding.root.context.getColor(R.color.onColorError))
            snackPopup.show()
        }

        private fun artistsToPrettyString(artists: List<Artist>?): String {
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
}
