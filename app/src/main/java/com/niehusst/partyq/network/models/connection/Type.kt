package com.niehusst.partyq.network.models.connection

/**
 * Identifiers for different types of packets sent via Nearby Connections
 * between host and guests.
 */
enum class Type {
    QUERY,
    SEARCH_RESULT,
    UPDATE_QUEUE,
    ENQUEUE,
    SKIP_VOTE
}
