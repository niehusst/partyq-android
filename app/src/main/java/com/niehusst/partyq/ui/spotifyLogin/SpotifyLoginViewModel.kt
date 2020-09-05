package com.niehusst.partyq.ui.spotifyLogin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niehusst.partyq.repository.SpotifyRepository
import kotlinx.coroutines.launch

class SpotifyLoginViewModel : ViewModel() {

    /**
     * Delegate to SpotifyAuthenticationService, allowing later access to AppRemote connection
     */
    fun connectToSpotify(
        context: Context?,
        onConnectCallback: (() -> Unit)? = null,
        onFailCallback: (() -> Unit)? = null
    ) {
        // TODO: emit loading and success states
        viewModelScope.launch {
            SpotifyRepository
                .authenticateWithSpotfiy(context, onConnectCallback, onFailCallback)
        }
    }
}
