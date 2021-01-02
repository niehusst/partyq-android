/*
 * Copyright 2020 Liam Niehus-Staab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.niehusst.partyq.services

import android.util.Base64

class KeyFetchService {
    companion object {

        init {
            System.loadLibrary("keys")
        }

        private external fun getSpotifyClientId(): String
        private external fun getSpotifyClientSecret(): String

        /**
         * Fetch the Spotify client ID key from a location slightly more secure than
         * hardcoded right here. It is not included in the source on Github (srry).
         * If you would like to test code pulled from here on your own, you'll have
         * to make your own Spotify developer account and get your own client ID.
         *
         * @return SPOTIFY_CLIENT_ID - secret key string for authenticating/connecting to
         *                          the Spotify app via AppRemote SDK
         */
        fun getSpotifyId(): String {
            // if running on circleci, load key from env var
            val envVar = System.getenv("SPOTIFY_CLIENT_ID")
            if (!envVar.isNullOrEmpty()) {
                return envVar
            }
            // otherwise fetch from "secure location"
            return String(Base64.decode(getSpotifyClientId(), Base64.DEFAULT))
        }

        fun getSpotifySecret(): String {
            // TODO update getting started wiki to include new func
            return String(Base64.decode(getSpotifyClientSecret(), Base64.DEFAULT))
        }
    }
}
