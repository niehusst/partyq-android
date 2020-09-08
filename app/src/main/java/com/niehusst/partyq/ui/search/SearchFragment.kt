package com.niehusst.partyq.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
    //TODO: use DiffUtil eventually?? is that faster?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchResults.adapter = adapter
        
    }
}
