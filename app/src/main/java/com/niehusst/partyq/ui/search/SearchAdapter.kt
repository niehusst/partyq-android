package com.niehusst.partyq.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.niehusst.partyq.databinding.SearchResultItemBinding
import com.niehusst.partyq.network.models.*
import com.niehusst.partyq.services.PartyCodeHandler

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
                // TODO: add to queue
            }
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
