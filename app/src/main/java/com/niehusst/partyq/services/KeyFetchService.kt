package com.niehusst.partyq.services

import android.util.Base64

class KeyFetchService {
    companion object {

        init {
            System.loadLibrary("keys")
        }

        private external fun getSpotifyClientKey(): String

        /**
         * Fetch the Spotify client ID key from a location slightly more secure than
         * hardcoded right here. It is not included in the source on Github (srry).
         * If you would like to test code pulled from here on your own, you'll have
         * to make your own Spotify developer account and get your own client ID.
         *
         * @return SPOTIFY_CLIENT_ID - secret key string for authenticating/connecting to
         *                          the Spotify app via AppRemote SDK
         */
        fun getSpotifyKey(): String {
            // if running on circleci, load key from env var
            val envVar = System.getenv("SPOTIFY_CLIENT_ID")
            if (!envVar.isNullOrEmpty()) {
                return envVar
            }
            // otherwise fetch from "secure location"
            return String(Base64.decode(getSpotifyClientKey(), Base64.DEFAULT))
        }
    }
}
