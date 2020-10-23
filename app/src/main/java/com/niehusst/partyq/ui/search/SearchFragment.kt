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


class SearchFragment : Fragment() {

    private lateinit var binding: SearchFragmentBinding
    private val viewModel by viewModels<SearchFragmentViewModel>()
    private val adapter = SearchAdapter()

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
        binding.searchResults.adapter = adapter
        setObservers()
        setSearchListener()
    }

    private fun setObservers() {
        SearchResultHandler.results.observe(viewLifecycleOwner, Observer {
            adapter.searchResults = it
            binding.isResults = it.isNotEmpty()
            adapter.notifyDataSetChanged()
        })
        SearchResultHandler.status.observe(viewLifecycleOwner, Observer {
            when(it) {
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
    // TODO: add paging ability

    private fun setSearchListener() {
        binding.searchBar.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // send query to Spotify
                viewModel.submitQuery(v.text.toString(), requireContext())
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
