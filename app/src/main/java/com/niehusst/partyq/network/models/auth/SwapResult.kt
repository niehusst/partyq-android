package com.niehusst.partyq.network.models.auth

import com.squareup.moshi.Json

data class SwapResult(
    @field:Json(name = "access_token")
    val accessToken: String,
    @field:Json(name = "token_type")
    val tokenType: String,
    @field:Json(name = "refresh_token")
    val refreshToken: String,
    @field:Json(name = "expires_in")
    val secondsUntilExpiration: Int
)
