package com.niehusst.partyq.network.models

import com.squareup.moshi.Json

data class ExternalUrls(
    @Json(name = "spotify")
    val spotify: String
)
