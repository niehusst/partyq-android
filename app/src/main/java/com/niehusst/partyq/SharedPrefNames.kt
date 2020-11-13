package com.niehusst.partyq

object SharedPrefNames {
    // name of the app's shared prefs file
    const val PREFS_FILE_NAME = "com.niehusst.partyq.sharedprefs"
    // String - the Spotify API OAuth access token
    const val ACCESS_TOKEN = "access_token"
    // Long - time in millis when the Spotify API OAuth access token expires
    const val EXPIRES_AT = "expires_at"
    // String - the 4 digit code for the last party the user joined
    const val PARTY_CODE = "party_code"
    // String - the 4 digit code of the last party the user hosted
    const val IS_HOST = "is_host"
    // Boolean - indicates whether the user has just joined a party
    const val PARTY_FIRST_START = "party_first_start"
}
