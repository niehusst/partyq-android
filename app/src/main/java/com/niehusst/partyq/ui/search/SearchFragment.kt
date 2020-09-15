package com.niehusst.partyq.ui.search

import android.app.Activity
import android.content.Context
import android.os.Bundle
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
                hideKeyboardFrom(v)
                viewModel.submitQuery(v.text.toString())
                Toast.makeText(requireContext(), "Entered serach ${v.text.toString()}", Toast.LENGTH_SHORT).show()
            }
            return@setOnEditorActionListener true
        }
    }

    fun hideKeyboardFrom(view: View) {
        val imm: InputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
