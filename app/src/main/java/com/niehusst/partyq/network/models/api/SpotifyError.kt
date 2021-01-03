package com.niehusst.partyq.network.models.api

import com.squareup.moshi.Json

data class SpotifyError(
    @Json(name = "status")
    val status: Int,
    @Json(name = "message")
    val message: String
)
