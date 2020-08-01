package com.niehusst.partyq.ui.spotifyLogin

import android.content.Context
import androidx.lifecycle.ViewModel
import com.niehusst.partyq.services.SpotifyAuthenticationService

class SpotifyLoginViewModel : ViewModel() {

    /**
     * Delegate to SpotifyAuthenticationService, allowing later access to AppRemote connection
     */
    fun connectToSpotify(
        context: Context?,
        onConnectCallback: (() -> Unit)? = null,
        onFailCallback: (() -> Unit)? = null
    ) {
        SpotifyAuthenticationService
            .authenticateWithSpotfiy(context, onConnectCallback, onFailCallback)
    }
}