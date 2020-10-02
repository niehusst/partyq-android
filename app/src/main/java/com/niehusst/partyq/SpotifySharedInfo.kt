package com.niehusst.partyq

object SpotifySharedInfo {
    // unused deeplink URI. Nevertheless required for Spotify auth
    const val REDIRECT_URI = "com.niehusst.partyq://callback"
    // can be any int. Simply for verifying response from API
    const val REQUEST_CODE = 42069
}
