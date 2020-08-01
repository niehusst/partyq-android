package com.niehusst.partyq.services

import android.content.Context
import com.spotify.android.appremote.api.SpotifyAppRemote

interface SpotifyAuthenticationRepository {

    fun getSpotifyAppRemote(): SpotifyAppRemote?

    fun authenticateWithSpotfiy(
        context: Context?,
        onConnectCallback: (() -> Unit)? = null,
        onFailCallback: (() -> Unit)? = null
    )
}
