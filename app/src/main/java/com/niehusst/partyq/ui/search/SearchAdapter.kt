package com.niehusst.partyq.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.niehusst.partyq.databinding.SearchResultItemBinding
import com.niehusst.partyq.network.models.*
import com.niehusst.partyq.services.CommunicationService

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ResultViewHolder>() {

    var searchResults = mutableListOf<Item>(
//        Item(
//            Album(
//                albumType = "",
//                externalUrls = ExternalUrls(""),
//                href = "",
//                id = "",
//                name = "bal",
//                releaseDate = "",
//                releaseDatePrecision = "",
//                totalTracks = 2,
//                type = "",
//                images = listOf(
//                    Image(
//                        url = "https://i.scdn.co/image/8522fc78be4bf4e83fea8e67bb742e7d3dfe21b4",
//                        width = 64,
//                        height = 64
//                    )
//                ),
//                uri = ""
//            ),
//            artists = listOf(
//                Artist(
//                    externalUrls = ExternalUrls(""),
//                    href = "",
//                    id = "",
//                    name = "Smash mouth",
//                    type = "",
//                    uri = ""
//                ),
//                Artist(
//                    externalUrls = ExternalUrls(""),
//                    href = "",
//                    id = "",
//                    name = "poopy",
//                    type = "",
//                    uri = ""
//                )
//            ),
//            discNumber = 0,
//            durationMs = 0,
//            explicit = false,
//            externalIds = ExternalIds("a"),
//            externalUrls = ExternalUrls(""),
//            href = "",
//            id = "aa",
//            isLocal = false,
//            name = "All Star",
//            popularity = 0,
//            trackNumber = 1,
//            uri = "",
//            type = ""
//        )
    )

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
                CommunicationService.updateQueue(item)
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
