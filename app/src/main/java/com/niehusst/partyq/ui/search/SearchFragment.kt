package com.niehusst.partyq.ui.search

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.niehusst.partyq.databinding.SearchFragmentBinding
import com.niehusst.partyq.network.Status


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
        return binding.root
    }
    //TODO: use DiffUtil/DiffAdapter eventually?? is that faster?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchResults.adapter = adapter
        setObservers()
        setSearchListener()
    }

    private fun setObservers() {
        viewModel.isResult.observe(viewLifecycleOwner, Observer {
            binding.isResults = it
        })
        viewModel.result.observe(viewLifecycleOwner, Observer {
            adapter.searchResults = it
            Log.e("BIGGYCHEESE", "$it")
            adapter.notifyDataSetChanged()
        })
        viewModel.status.observe(viewLifecycleOwner, Observer {
            when(it) {
                Status.LOADING -> {
                    // TODO: trigger spinner
                    Toast.makeText(requireContext(), "loading", Toast.LENGTH_SHORT).show()
                }
                Status.ERROR -> {
                    // TODO: trigger snackbar or something?
                    Toast.makeText(requireContext(), "errrrr", Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS -> {
                    // TODO: untrigger loading stuff. Set text "No results"
                    Toast.makeText(requireContext(), "RESULT!!!!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // TODO: show "enter search" text
                    Toast.makeText(requireContext(), "Entered nothing", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

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
