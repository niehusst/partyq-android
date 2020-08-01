package com.niehusst.partyq.ui.spotifyLogin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.niehusst.partyq.services.SpotifyAuthenticationRepository

class SpotifyLoginViewModel(private val spotifyRepository: SpotifyAuthenticationRepository) : ViewModel() {

    /**
     * Delegate to SpotifyAuthenticationService, allowing later access to AppRemote connection
     */
    fun connectToSpotify(
        context: Context?,
        onConnectCallback: (() -> Unit)? = null,
        onFailCallback: (() -> Unit)? = null
    ) {
        spotifyRepository
            .authenticateWithSpotfiy(context, onConnectCallback, onFailCallback)
    }

    @Suppress("UNCHECKED_CAST")
    class SpotifyLoginViewModelFactory(
        private val spotifyRepository: SpotifyAuthenticationRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return (SpotifyLoginViewModel(spotifyRepository) as T)
        }
    }
}
