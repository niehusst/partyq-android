package com.niehusst.partyq.services

import android.util.Base64

class KeyFetchService {
    companion object {

        init {
            System.loadLibrary("keys")
        }

        private external fun getSpotifyClientKey(): String

        fun getSpotifyKey(): String {
            // TODO: return env var if in circleci?
            return String(Base64.decode(getSpotifyClientKey(), Base64.DEFAULT))
        }
    }
}
