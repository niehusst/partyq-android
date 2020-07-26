package com.niehusst.partyq.ui.mainActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.niehusst.partyq.R
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // setup action bar
        setSupportActionBar(findViewById(R.id.toolbar))
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // from the launch activity, everyone starts as a guest
        menuInflater.inflate(R.menu.toolbar_menu_guest, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.aboutFragment -> {
                NavigationUI.onNavDestinationSelected(item, nav_host_fragment.findNavController())
                true
            }
            R.id.legalFragment -> {
                NavigationUI.onNavDestinationSelected(item, nav_host_fragment.findNavController())
                true
            }
            else -> {
                Timber.e("Menu item not found")
                false
            }
        }
    }

//    private val clientId = System.getenv("SPOTIFY_CLIENT_ID") ?: "default
//    private val redirectUri = "com.niehusst.partyq://callback"
//    private var spotifyAppRemote: SpotifyAppRemote? = null
//
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
