package com.niehusst.partyq.network.models.connection

/**
 * Wrapper for any payload sent via Nearby Connections API.
 *
 * @param type - identifies of what type of data `payload` contains
 * @param payload - the data (in JSON form) being delivered via Nearby Connections API
 */
data class ConnectionPayload(
    val type: Type,
    val payload: String
)
