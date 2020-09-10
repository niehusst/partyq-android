package com.niehusst.partyq.network.models

import com.squareup.moshi.Json

data class ExternalIds(
    @Json(name = "isrc")
    val isrc: String
)
