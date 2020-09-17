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
