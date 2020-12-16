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

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.SearchPageItemBinding
import com.niehusst.partyq.databinding.SearchResultItemBinding
import com.niehusst.partyq.network.models.api.Item
import com.niehusst.partyq.network.models.api.Tracks
import com.niehusst.partyq.repository.SpotifyRepository
import com.niehusst.partyq.services.QueueService
import com.niehusst.partyq.services.UserTypeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                pageNum ?: 1,
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

            binding.nextPage.setOnClickListener {
                nextPageUrl?.run {
                    performSearch(nextPageUrl)
                }
            }

            binding.prevPage.setOnClickListener {
                prevPageUrl?.run {
                    performSearch(prevPageUrl)
                }
            }
        }

        private fun performSearch(url: String) {
            // TODO somehow tie to viewLifeCycle? viewmodel? custom coroutine scope?
            GlobalScope.launch(Dispatchers.IO) {
                SpotifyRepository.searchSongsForLocalResult(url, isHost, isPaged = true)
            }
        }
    }

    companion object {
        const val TYPE_FOOTER = 0
        const val TYPE_ITEM = 1
    }
}
