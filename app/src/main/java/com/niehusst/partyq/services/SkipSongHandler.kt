package com.niehusst.partyq.services

object SkipSongHandler {

    private var skipCount = 0

    /**
     * Register a single vote to skip the currently playing song. The song will only be skipped
     * if more than 50% of party goers have voted to skip since the last call to `clearSkipCount()`
     */
    fun voteSkip() {
        // +1 to include the host in the count
        val numPartyGoers = CommunicationService.connectionEndpointIds.size + 1

        if (++skipCount > numPartyGoers / 2) {
            // skip song
            SpotifyPlayerService.skipSong()
            // clearing count is handled by autoplay in SpotifyPlayerService
        }
    }

    fun clearSkipCount() {
        skipCount = 0
    }
}
