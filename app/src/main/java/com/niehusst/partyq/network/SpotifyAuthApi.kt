package com.niehusst.partyq.network

import android.util.Base64
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class SpotifyAuthApi(
    private val clientId: String,
    private val clientSecret: String
) {

    private val SPOTIFY_AUTH_API_BASE_URL = "https://accounts.spotify.com/"
    var endPoints: SpotifyAuthApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(SPOTIFY_AUTH_API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(buildAuthInterceptor())
            .build()
        endPoints = retrofit.create(SpotifyAuthApiService::class.java)
    }

    private fun buildAuthInterceptor(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
            val ogRequest = chain.request()
            // add the OAuth token to all out-going API calls
            val reqBuilder = ogRequest.newBuilder()
                .header("Authorization", "Basic ${Base64.encodeToString("$clientId:$clientSecret".toByteArray(), Base64.DEFAULT)}")
            chain.proceed(reqBuilder.build())
        }

        return httpClient.build()
    }
}
