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

package com.niehusst.partyq.ui.search

import androidx.lifecycle.*
import com.niehusst.partyq.network.Status
import com.niehusst.partyq.repository.SpotifyRepository
import com.niehusst.partyq.services.SearchResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFragmentViewModel : ViewModel() {

    fun submitQuery(query: String?, isHost: Boolean) {
        if (query.isNullOrEmpty()) {
            return
        }

        SearchResultHandler.setStatus(Status.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            SpotifyRepository.searchSongsForLocalResult(query, isHost, isPaged = false)
        }
    }

    fun pagedSearch(url: String, isHost: Boolean) {
        SearchResultHandler.setStatus(Status.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            SpotifyRepository.searchSongsForLocalResult(url, isHost, isPaged = true)
        }
    }
}
