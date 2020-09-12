package com.niehusst.partyq.ui.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.niehusst.partyq.databinding.SearchFragmentBinding

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
        binding.searchBar.clearFocus() // clear initial focus on EditText
        setObserver()
        setSearchListener()
    }

    private fun setObserver() {
        viewModel.isResult.observe(viewLifecycleOwner, Observer {
            binding.isResults = it
        })
    }

    private fun setSearchListener() {
        binding.searchBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // send query to Spotify
                // TODO: start loading spinner
                // TODO: close keyboard
                viewModel.submitQuery(v.text.toString())
                Toast.makeText(requireContext(), "Entered serach", Toast.LENGTH_SHORT).show()
            }
            return@setOnEditorActionListener true
        }
    }
}
