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

package com.niehusst.partyq.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class SpotifyApi(
    private val accessToken: String
) {

    private val SPOTIFY_API_BASE_URL = "https://api.spotify.com/"
    var endPoints: SpotifyApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(SPOTIFY_API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(buildAuthInterceptor())
            .build()
        endPoints = retrofit.create(SpotifyApiService::class.java)
    }

    private fun buildAuthInterceptor(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
            val ogRequest = chain.request()
            // add the OAuth token to all out-going API calls
            val reqBuilder = ogRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
            chain.proceed(reqBuilder.build())
        }

        return httpClient.build()
    }
}
