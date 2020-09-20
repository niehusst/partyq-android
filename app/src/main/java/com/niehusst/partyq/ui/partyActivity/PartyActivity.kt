package com.niehusst.partyq.ui.partyActivity

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.niehusst.partyq.R
import com.niehusst.partyq.SharedPrefNames.PARTY_FIRST_START
import com.niehusst.partyq.SharedPrefNames.PREFS_FILE_NAME
import com.niehusst.partyq.databinding.ActivityPartyBinding
import com.niehusst.partyq.repository.SpotifyRepository
import timber.log.Timber

class PartyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPartyBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_party)
        setupActionBar()
        setupBottomNavBinding()
        startConnectionService()
        startSpotifyPlayerService()

        // set search as first active tab
        binding.bottomNav.selectedItemId = R.id.searchFragment
    }
    // TODO: display errors in fragment somehow. (toast? put as bg text? snackbar?)

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_loggedin, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.leave_party_item -> {
                leaveParty()
                true
            }
            R.id.party_code_item -> {
                launchPartyCodeDialog()
                true
            }
            R.id.aboutFragment,
            R.id.legalFragment -> NavigationUI.onNavDestinationSelected(item, navController)
            else -> {
                Timber.e("Menu item not found")
                false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onStart() {
        super.onStart()
        val prefs = this.applicationContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean(PARTY_FIRST_START, false)) {
            launchPartyCodeDialog()
            prefs.edit()
                .putBoolean(PARTY_FIRST_START, false)
                .apply()
        }
    }

    private fun setupBottomNavBinding() {
        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.searchFragment,
                R.id.queueFragment,
                R.id.nowPlayingFragment -> {
                    true
                    // TODO: implement fragments
//                    NavigationUI
//                        .onNavDestinationSelected(item, party_nav_host_fragment.findNavController())
                }
                else -> {
                    Timber.e("Bottom Nav selection ${item.title} not recognized")
                    false
                }
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.party_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    private fun startConnectionService() {
        // TODO: delegate host vs guest logic to the repo/service
        // TODO: should i worry about accidentally starting service multiple times? could happen on process death recovery?
    }

    private fun startSpotifyPlayerService() {
        // TODO: ? need any more than this? if music playing requires more setup, do that here
        SpotifyRepository.start(this)
    }

    private fun launchPartyCodeDialog() {
        navController.navigate(R.id.partyCodeFragment)
    }

    private fun leaveParty() {
        // TODO:
    }
}
