package com.niehusst.partyq.ui.fakes

import android.content.Context
import com.niehusst.partyq.services.SpotifyAuthenticationRepository
import com.spotify.android.appremote.api.SpotifyAppRemote

class SpotifyRepositoryFake : SpotifyAuthenticationRepository {

    override fun getSpotifyAppRemote(): SpotifyAppRemote? {
        return null
    }

    override fun authenticateWithSpotfiy(
        context: Context?,
        onConnectCallback: (() -> Unit)?,
        onFailCallback: (() -> Unit)?
    ) {}
}
