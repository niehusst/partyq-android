package com.niehusst.partyq.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.niehusst.partyq.network.Status
import com.niehusst.partyq.network.models.api.Item
import com.niehusst.partyq.network.models.api.SearchResult

object SearchResultHandler {

    private val _status = MutableLiveData(Status.NO_ACTION)
    val status: LiveData<Status> = _status

    private val _results = MutableLiveData<List<Item>>(listOf())
    val results: LiveData<List<Item>> = _results

    fun receiveSearchResults(res: SearchResult) {
        _status.value = Status.SUCCESS
        // get just the track items. Filter out duplicate URIs
        var songs = res?.tracks?.items ?: listOf()
        songs = songs.distinctBy { it.uri }
        _results.value = songs
    }

    fun setStatus(status: Status) {
        _status.value = status
    }
}
