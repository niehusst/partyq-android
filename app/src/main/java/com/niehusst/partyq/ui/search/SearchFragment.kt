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

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.SearchFragmentBinding
import com.niehusst.partyq.network.Status
import com.niehusst.partyq.services.SearchResultHandler
import com.niehusst.partyq.services.UserTypeService

class SearchFragment : Fragment() {

    private lateinit var binding: SearchFragmentBinding
    private val viewModel by viewModels<SearchFragmentViewModel>()
    private lateinit var adapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SearchFragmentBinding.inflate(layoutInflater)
        binding.isResults = false
        binding.loading = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SearchAdapter(viewModel)
        binding.searchResults.adapter = adapter
        setObservers()
        setSearchListener()
    }

    private fun setObservers() {
        SearchResultHandler.searchResultSongs.observe(viewLifecycleOwner, Observer {
            // load search result data into the search adapter
            adapter.searchResults = it
            adapter.searchPage = SearchResultHandler.searchResult
            binding.isResults = it.isNotEmpty()
            adapter.notifyDataSetChanged()
            binding.searchResults.scrollToPosition(0)
        })
        SearchResultHandler.status.observe(viewLifecycleOwner, Observer {
            when (it) {
                Status.LOADING -> binding.loading = true
                Status.ERROR -> {
                    // TODO: trigger snackbar or something w/ more detail?
                    binding.loading = false
                    binding.noResultsText.text = context?.resources?.getString(R.string.error_face)
                }
                Status.SUCCESS -> {
                    binding.loading = false
                    binding.noResultsText.text = context?.resources?.getString(R.string.no_results)
                }
                else -> binding.noResultsText.text = context?.resources?.getString(R.string.search_for_songs)
            }
        })
    }

    private fun setSearchListener() {
        binding.searchBar.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // send query to Spotify
                viewModel.submitQuery(v.text.toString(), UserTypeService.isHost(requireContext()))
                hideKeyboard(v)
            }
            return@setOnEditorActionListener true
        }
    }

    private fun hideKeyboard(view: View) {
        val imm: InputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
