package com.niehusst.partyq.network.models

import com.squareup.moshi.Json

data class SearchResult(
    @Json(name = "tracks")
    val tracks: Tracks
)
