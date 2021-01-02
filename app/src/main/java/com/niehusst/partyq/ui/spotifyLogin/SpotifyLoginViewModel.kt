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

package com.niehusst.partyq.ui.spotifyLogin

import android.app.Activity
import androidx.lifecycle.*
import com.niehusst.partyq.network.models.auth.SwapResult
import com.niehusst.partyq.repository.SpotifyAuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class SpotifyLoginViewModel : ViewModel() {

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    /**
     * Delegate to SpotifyAuthenticationService, allowing later access to AppRemote connection
     */
    fun connectToSpotify(handlerActivity: Activity) {
        _loading.value = true
        SpotifyAuthRepository
            .authenticateWithSpotfiy(handlerActivity)
    }

    fun stopLoading() {
        _loading.postValue(false)
    }

    fun swapCodeForTokenAsync(code: String): Deferred<SwapResult?> {
        return viewModelScope.async(Dispatchers.IO) {
            return@async SpotifyAuthRepository.getAuthTokens(code)
        }
    }
}
