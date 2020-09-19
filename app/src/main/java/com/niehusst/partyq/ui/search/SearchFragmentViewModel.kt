package com.niehusst.partyq.ui.search

import android.content.Context
import androidx.lifecycle.*
import com.niehusst.partyq.network.Status
import com.niehusst.partyq.network.models.Item
import com.niehusst.partyq.repository.SpotifyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFragmentViewModel : ViewModel() {

    private val _status = MutableLiveData<Status?>(null)
    val status: LiveData<Status?> = _status

    private val _isResult = MutableLiveData(false)
    val isResult: LiveData<Boolean> = _isResult

    private val _result = MutableLiveData(listOf<Item>())
    val result: LiveData<List<Item>> = _result

    fun submitQuery(query: String?, context: Context) {
        if (query.isNullOrEmpty()) {
            _isResult.value = false
            _result.value = listOf()
            return
        }

        _status.value = Status.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            val res = SpotifyRepository.searchSongs(query, context)
            _status.postValue(res.status)
            // get just the track items. Filter out duplicate URIs
            var songs = res.data?.tracks?.items ?: listOf()
            songs = songs.distinctBy { it.uri }
            _result.postValue(songs)
            _isResult.postValue(songs.isNotEmpty())
        }
    }
}
