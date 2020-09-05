package com.niehusst.partyq.ui.spotifyLogin

import android.content.Context
import androidx.lifecycle.*
import com.niehusst.partyq.repository.SpotifyRepository

class SpotifyLoginViewModel : ViewModel() {

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    /**
     * Delegate to SpotifyAuthenticationService, allowing later access to AppRemote connection
     */
    fun connectToSpotify(
        context: Context?,
        onConnectCallback: (() -> Unit)? = null,
        onFailCallback: (() -> Unit)? = null
    ) {
        _loading.value = true
        SpotifyRepository
            .authenticateWithSpotfiy(context, onConnectCallback, onFailCallback)
    }

    fun stopLoading() {
        _loading.value = false
    }
}
