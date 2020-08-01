package com.niehusst.partyq

import android.app.Application
import com.niehusst.partyq.services.SpotifyAuthenticationRepository
import timber.log.Timber

class PartyqApplication : Application() {

    val spotifyAuthRepository: SpotifyAuthenticationRepository
        get() = ServiceLocator.provideSpotifyRepository()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
