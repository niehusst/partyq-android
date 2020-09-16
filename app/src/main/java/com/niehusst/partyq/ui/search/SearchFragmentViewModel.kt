package com.niehusst.partyq.ui.search

import android.content.Context
import androidx.lifecycle.*
import com.niehusst.partyq.network.Resource
import com.niehusst.partyq.network.models.SearchResult
import com.niehusst.partyq.repository.SpotifyRepository
import kotlinx.coroutines.launch

class SearchFragmentViewModel : ViewModel() {

    private val _isResult = MutableLiveData(false)
    val isResult: LiveData<Boolean> = _isResult

    private val _result = MutableLiveData<Resource<SearchResult>>()
    val result: LiveData<Resource<SearchResult>> = _result

    fun submitQuery(query: String?, context: Context) {
        query ?: return //TODO: clear adapter?

        viewModelScope.launch {
            val res = SpotifyRepository.searchSongs(query, context)
            // fucking map or switchmap or soemthign to put res into _result???
            _result.switchMap { _ -> res }

            //if(results.size > 0) {
            _isResult.value = true
            //} else {
            //    _isResult.value = false
            //}
            // TODO: update the adapter from the result livedata?
            // TODO: display errors in fragment somehow. (toast? put as bg text? snackbar?)
        }
    }
}
