package com.niehusst.partyq.ui.mainActivity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.niehusst.partyq.R
import com.niehusst.partyq.services.SpotifyAuthenticator
import com.niehusst.partyq.ui.spotifyLogin.SpotifyLoginFragment
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
        menuInflater.inflate(R.menu.toolbar_menu_pre_login, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.aboutFragment,
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SpotifyAuthenticator.REQUEST_CODE) {
            // let the visible instance of SpotifyLoginFragment handle the rest of auth req
            val fragment = supportFragmentManager.fragments.firstOrNull()
                ?.childFragmentManager?.fragments?.firstOrNull { it.isVisible }
            (fragment as? SpotifyLoginFragment)?.onAuthResult(resultCode, data)
        }
    }
}
