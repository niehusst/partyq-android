package com.niehusst.partyq.ui.search

import android.content.Context
import androidx.lifecycle.*
import com.niehusst.partyq.network.Status
import com.niehusst.partyq.repository.SpotifyRepository
import com.niehusst.partyq.services.SearchResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFragmentViewModel : ViewModel() {

    fun submitQuery(query: String?, context: Context) {
        if (query.isNullOrEmpty()) {
            return
        }

        SearchResultHandler.setStatus(Status.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            SpotifyRepository.searchSongsForLocalResult(query, context)
        }
    }
}
