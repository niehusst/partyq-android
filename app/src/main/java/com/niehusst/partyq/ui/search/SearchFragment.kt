package com.niehusst.partyq.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.niehusst.partyq.databinding.SearchFragmentBinding

class SearchFragment : Fragment() {

    /*
    TODO: redo UI design? add options dropdown menu (to allow easier party disconnection) and
    make the search bar a menu icon that clicking adds a SearchView above the RecyclerView in the layout
     */
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
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false // let defaults handle suggestion
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                // send query to Spotify
                // TODO: loading spinner
                // TODO: close keyboard
                viewModel.submitQuery(query)
                return true
            }
        })
    }
}
