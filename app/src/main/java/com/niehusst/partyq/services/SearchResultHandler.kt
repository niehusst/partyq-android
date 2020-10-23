package com.niehusst.partyq.services

import android.util.Log
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

    fun updateSearchResults(res: SearchResult) {
        // get just the track items. Filter out duplicate URIs
        var songs = res.tracks?.items ?: listOf()
        songs = songs.distinctBy { it.uri }
        _results.postValue(songs)
    }

    /**
     * Thread-safe set value of status LiveData
     */
    fun setStatus(status: Status) {
        _status.postValue(status)
    }
}
