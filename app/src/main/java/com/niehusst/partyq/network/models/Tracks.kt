package com.niehusst.partyq.network.models

import com.squareup.moshi.Json

data class Tracks(
    @Json(name = "href")
    val href: String,
    @Json(name = "items")
    val items: List<Item>? = null,
    @Json(name = "limit")
    val limit: Int,
    @Json(name = "next")
    val next: String,
    @Json(name = "offset")
    val offset: Int,
    @Json(name = "previous")
    val previous: Int? = null,
    @Json(name = "total")
    val total: Int
)
