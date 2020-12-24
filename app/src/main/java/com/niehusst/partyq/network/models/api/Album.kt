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

data class Album(
    @field:Json(name = "album_type")
    val albumType: String,
    @Json(name = "artists")
    val artists: List<Artist>? = null,
    @field:Json(name = "available_markets")
    val availableMarkets: List<String>? = null,
    @field:Json(name = "external_urls")
    val externalUrls: ExternalUrls?,
    @Json(name = "href")
    val href: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "images")
    val images: List<Image>? = null,
    @Json(name = "name")
    val name: String,
    @field:Json(name = "release_date")
    val releaseDate: String,
    @field:Json(name = "release_date_precision")
    val releaseDatePrecision: String,
    @field:Json(name = "total_tracks")
    val totalTracks: Int,
    @Json(name = "type")
    val type: String,
    @Json(name = "uri")
    val uri: String
)
