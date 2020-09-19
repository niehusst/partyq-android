package com.niehusst.partyq.ui.spotifyLogin

import android.app.Activity
import androidx.lifecycle.*
import com.niehusst.partyq.services.SpotifyAuthenticator

class SpotifyLoginViewModel : ViewModel() {

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    /**
     * Delegate to SpotifyAuthenticationService, allowing later access to AppRemote connection
     */
    fun connectToSpotify(handlerActivity: Activity) {
        _loading.value = true
        SpotifyAuthenticator
            .authenticateWithSpotfiy(handlerActivity)
    }

    fun stopLoading() {
        _loading.value = false
    }
}
