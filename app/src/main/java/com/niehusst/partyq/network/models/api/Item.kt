/*
 * Copyright 2020 Liam Niehus-Staab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.niehusst.partyq.network.models.api

import com.squareup.moshi.Json
// TODO: cut down on unused fields to make payload smaller?
data class Item(
    @Json(name = "album")
    val album: Album,
    @Json(name = "artists")
    val artists: List<Artist>? = null,
    @field:Json(name = "available_markets")
    val availableMarkets: List<String>? = null,
    @field:Json(name = "disc_number")
    val discNumber: Int,
    @field:Json(name = "duration_ms")
    val durationMs: Int,
    @Json(name = "explicit")
    val explicit: Boolean,
    @field:Json(name = "external_ids")
    val externalIds: ExternalIds?,
    @field:Json(name = "external_urls")
    val externalUrls: ExternalUrls?,
    @Json(name = "href")
    val href: String,
    @Json(name = "id")
    val id: String,
    @field:Json(name = "is_local")
    val isLocal: Boolean,
    @Json(name = "name")
    val name: String,
    @Json(name = "popularity")
    val popularity: Int,
    @field:Json(name = "preview_url")
    val previewUrl: String? = null,
    @field:Json(name = "track_number")
    val trackNumber: Int,
    @Json(name = "type")
    val type: String,
    @Json(name = "uri")
    val uri: String
) {
    fun artistsAsPrettyString(): String {
        var nameList = ""
        var i = 0
        artists?.forEach { art ->
            nameList += art.name
            if (i < artists.size-1) {
                nameList += ", "
            }
            i++
        }
        return nameList
    }

    fun getSpotifyLink(): String {
        return listOf(
            externalUrls?.spotify,
            album?.externalUrls?.spotify,
            artists?.firstOrNull()?.externalUrls?.spotify,
            album?.artists?.firstOrNull()?.externalUrls?.spotify,
            href // last ditch effort to put out some spotify url
        ).firstOrNull { it != null } ?: "N/A"
    }
}
