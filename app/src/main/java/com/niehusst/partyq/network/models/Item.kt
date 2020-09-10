package com.niehusst.partyq.network.models

import com.squareup.moshi.Json

data class Item(
    @Json(name = "album")
    val album: Album,
    @Json(name = "artists")
    val artists: List<Artist>? = null,
    @Json(name = "available_markets")
    val availableMarkets: List<String>? = null,
    @Json(name = "disc_number")
    val discNumber: Int,
    @Json(name = "duration_ms")
    val durationMs: Int,
    @Json(name = "explicit")
    val explicit: Boolean,
    @Json(name = "external_ids")
    val externalIds: ExternalIds,
    @Json(name = "external_urls")
    val externalUrls: ExternalUrls,
    @Json(name = "href")
    val href: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "is_local")
    val isLocal: Boolean,
    @Json(name = "name")
    val name: String,
    @Json(name = "popularity")
    val popularity: Int,
    @Json(name = "preview_url")
    val previewUrl: String? = null,
    @Json(name = "track_number")
    val trackNumber: Int,
    @Json(name = "type")
    val type: String,
    @Json(name = "uri")
    val uri: String
)
