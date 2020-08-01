package com.niehusst.partyq

import com.niehusst.partyq.services.SpotifyAuthenticationRepository
import com.niehusst.partyq.services.SpotifyRepository

object ServiceLocator {

    private val lock = Any()

    @Volatile
    var spotifyRepository: SpotifyAuthenticationRepository? = null

    fun provideSpotifyRepository(): SpotifyAuthenticationRepository {
        synchronized(lock) {
            return spotifyRepository ?: createSpotifyRepository()
        }
    }

    private fun createSpotifyRepository(): SpotifyAuthenticationRepository {
        return SpotifyRepository()
    }
}