/*
 * Copyright 2020 Liam Niehus-Staab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    fun updateSearchResults(res: SearchResult) {
        // get just the track items. Filter out duplicate URIs
        var songs = res.tracks?.items ?: listOf()
        songs = songs.distinctBy { it.uri }
        _results.postValue(songs)
    }

    fun setStatus(status: Status) {
        _status.postValue(status)
    }

    fun clearSearch() {
        _results.postValue(listOf())
        _status.postValue(Status.NO_ACTION)
    }
}
