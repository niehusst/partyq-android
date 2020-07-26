package com.niehusst.partyq.ui.mainActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.niehusst.partyq.BuildConfig
import com.niehusst.partyq.R
import timber.log.Timber

class MainActivity : AppCompatActivity() {

//    private val clientId = System.getenv("SPOTIFY_CLIENT_ID") ?: "default
//    private val redirectUri = "com.niehusst.partyq://callback"
//    private var spotifyAppRemote: SpotifyAppRemote? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // from the launch activity, everyone starts as a guest
        menuInflater.inflate(R.menu.toolbar_menu_guest, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.about_item -> {
                // TODO: perform navigation
                Toast.makeText(this, "clicked about", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.legal_item -> {
                // TODO: perform navigation
                Toast.makeText(this, "clicked legal", Toast.LENGTH_SHORT).show()
                true
            }
            else -> {
                Timber.e("Menu item not found")
                false
            }
        }
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
