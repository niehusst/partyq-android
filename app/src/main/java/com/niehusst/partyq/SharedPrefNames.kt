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

package com.niehusst.partyq

object SharedPrefNames {
    // name of the app's shared prefs file
    const val PREFS_FILE_NAME = "com.niehusst.partyq.sharedprefs"
    // String - the Spotify API OAuth access token
    const val ACCESS_TOKEN = "access_token"
    // String - the Spotify token that lets you refresh your OAuth ACCESS_TOKEN on expiration
    const val REFRESH_TOKEN = "refresh_token"
    // Long - time in millis when the Spotify API OAuth access token expires
    const val EXPIRES_AT = "expires_at"
    // String - the 4 digit code for the last party the user joined
    const val PARTY_CODE = "party_code"
    // String - the 4 digit code of the last party the user hosted
    const val IS_HOST = "is_host"
    // Boolean - indicates whether the user has just joined a party
    const val PARTY_FIRST_START = "party_first_start"
}
