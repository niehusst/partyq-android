package com.niehusst.partyq.network.models.connection

/**
 * Wrapper for any payload send via Nearby Connections in this app; enables identification of
 * what type of data is being sent via the `type` field, allowing the `payload` to be cast to the
 * correct type for usage.
 */
data class ConnectionPayload(
    val type: Type,
    val payload: Any?
)
