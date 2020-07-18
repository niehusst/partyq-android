package com.niehusst.partyq.mainActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.niehusst.partyq.BuildConfig
import com.niehusst.partyq.R
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import timber.log.Timber

class MainActivity : AppCompatActivity() {

//    private val clientId = BuildConfig.SPOTIFY_CLIENT_ID
//    private val redirectUri = "com.niehusst.partyq://callback"
//    private var spotifyAppRemote: SpotifyAppRemote? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

//    override fun onStart() {
//        super.onStart()
//        // Set the connection parameters
//        val connectionParams = ConnectionParams.Builder(clientId)
//            .setRedirectUri(redirectUri)
//            .showAuthView(true)
//            .build()
//
//        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
//            override fun onConnected(appRemote: SpotifyAppRemote) {
//                spotifyAppRemote = appRemote
//                Timber.d("Connected!")
//                // Now you can start interacting with App Remote
//                connected()
//            }
//
//            override fun onFailure(throwable: Throwable) {
//                Timber.e(throwable)
//                // Something went wrong when attempting to connect! Handle errors here
//                Toast.makeText(baseContext, "Error connecting to Spotify app", Toast.LENGTH_LONG).show()
//            }
//        })
//    }
//
//    private fun connected() {
//        // Play a playlist
//        spotifyAppRemote?.playerApi?.play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL")
//
//        // Subscribe to PlayerState
//        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
//            val track: Track = it.track
//            Timber.d( "${track.name} by ${track.artist.name}")
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//        // this doesn't actually stop SPOTIFY from running an playing music
//        spotifyAppRemote?.let {
//            SpotifyAppRemote.disconnect(it)
//        }
//    }
}
