package com.niehusst.partyq.network.models.api

import com.squareup.moshi.Json

data class ExternalIds(
    @Json(name = "isrc")
    val isrc: String
)
