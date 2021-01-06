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

package com.niehusst.partyq.services

import java.util.concurrent.TimeUnit

object TokenHandlerService {

    private var token: String? = null
    private var refreshToken: String? = null
    private var expiresAt: Long = 0L

    /**
     * Get the Spotify OAuth token or throw an Exception if it's invalid.
     *
     * @throws Exception when no token is saved locally or when token is expired
     * @return token - The Spotify OAuth token string for authenticating Spotify API calls
     */
    fun getAuthToken(): String {
        if (token == null || expiresAt < System.currentTimeMillis()) {
            throw Exception("Token is expired or doesn't exist")
        }
        return token!!
    }

    fun getRefreshToken(): String? {
        return refreshToken
    }

    fun setTokens(
        token: String,
        refreshToken: String,
        expiresIn: Int,
        unit: TimeUnit
    ) {
        val now = System.currentTimeMillis()
        expiresAt = now + unit.toMillis(expiresIn.toLong())
        this.token = token
        this.refreshToken = refreshToken
    }

    fun resetAuthToken(
        token: String,
        expiresIn: Int,
        unit: TimeUnit
    ) {
        val now = System.currentTimeMillis()
        expiresAt = now + unit.toMillis(expiresIn.toLong())
        this.token = token
    }

    fun tokenIsExpired(): Boolean {
        return System.currentTimeMillis() > expiresAt
    }

    fun clearToken() {
        token = null
        refreshToken = null
        expiresAt = 0L
    }
}
