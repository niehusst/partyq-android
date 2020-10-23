package com.niehusst.partyq.network.models.api

import com.squareup.moshi.Json

data class Artist(
    @Json(name = "external_urls")
    val externalUrls: ExternalUrls?,
    @Json(name = "href")
    val href: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "type")
    val type: String,
    @Json(name = "uri")
    val uri: String
)
