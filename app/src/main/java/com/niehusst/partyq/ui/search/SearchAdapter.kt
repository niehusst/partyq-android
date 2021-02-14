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

package com.niehusst.partyq.ui.search

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.SearchPageItemBinding
import com.niehusst.partyq.databinding.SearchResultItemBinding
import com.niehusst.partyq.network.models.api.Item
import com.niehusst.partyq.network.models.api.Tracks
import com.niehusst.partyq.services.QueueService
import com.niehusst.partyq.services.UserTypeService


class SearchAdapter(
    private val viewModel: SearchFragmentViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var searchPage: Tracks? = null
    var searchResults = listOf<Item>()


    override fun getItemViewType(position: Int): Int {
        return if (position >= searchResults.size) {
            TYPE_FOOTER
        } else {
            TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val isHost = UserTypeService.isHost(parent.context)

        return if (viewType == TYPE_FOOTER) {
            val binding = SearchPageItemBinding.inflate(layoutInflater, parent, false)
            PageSearchViewHolder(binding, isHost)
        } else { // TYPE_ITEM
            val binding = SearchResultItemBinding.inflate(layoutInflater, parent, false)
            ResultViewHolder(binding, isHost)
        }
    }

    override fun getItemCount(): Int {
        // +1 if results to account for the search_page_item layout at the end
        // pagination view not included if no results
        return if (searchResults.isNotEmpty()) { searchResults.size + 1 } else { 0 }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ResultViewHolder) {
            holder.bind(searchResults[position])
        } else {
            // to get the page num we're on, divide the index of first item in this request (offset)
            // by the number of items received per request (limit)
            val pageNum = searchPage?.let { it.offset / it.limit }

            (holder as PageSearchViewHolder).bind(
                pageNum ?: 0,
                searchPage?.next,
                searchPage?.previous
            )
        }
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

    inner class PageSearchViewHolder(
        private val binding: SearchPageItemBinding,
        private val isHost: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currPage: Int, nextPageUrl: String?, prevPageUrl: String?) {
            binding.pageNum.text = (currPage+1).toString()
            binding.firstPage = prevPageUrl == null
            binding.lastPage = nextPageUrl == null
            binding.isHost = isHost

            binding.nextPage.setOnClickListener {
                nextPageUrl?.run {
                    viewModel.pagedSearch(nextPageUrl, isHost)
                }
            }

            binding.prevPage.setOnClickListener {
                prevPageUrl?.run {
                    viewModel.pagedSearch(prevPageUrl, isHost)
                }
            }

            binding.openSpotifyButton.setOnClickListener {
                val linkIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = if (isHost) {
                        // deeplink open spotify
                        Uri.parse("spotify:")
                    } else {
                        // link to spotify app store listing
                        Uri.parse("http://play.google.com/store/apps/details?id=com.spotify.music")
                    }

                    // attribute partyq as the referrer
                    putExtra(
                        Intent.EXTRA_REFERRER,
                        Uri.parse("android-app://" + binding.root.context.packageName)
                    )
                }

                startActivity(
                    binding.root.context,
                    linkIntent,
                    null // no options
                )
            }
        }
    }

    companion object {
        const val TYPE_FOOTER = 0
        const val TYPE_ITEM = 1
    }
}
