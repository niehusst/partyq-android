package com.niehusst.partyq.ui.partyActivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.niehusst.partyq.R
import com.niehusst.partyq.SharedPrefNames.PARTY_FIRST_START
import com.niehusst.partyq.SharedPrefNames.PREFS_FILE_NAME
import com.niehusst.partyq.databinding.ActivityPartyBinding
import com.niehusst.partyq.extensions.setupWithNavController
import com.niehusst.partyq.repository.SpotifyRepository
import kotlinx.android.synthetic.main.activity_party.*
import timber.log.Timber

class PartyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPartyBinding
    private var currNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_party)
        setSupportActionBar(findViewById(R.id.toolbar))
        startConnectionService()
        startSpotifyPlayerService()
        if (savedInstanceState == null) {
            setupBottomNavBinding()
        } // else wait for onRestoreInstanceState

        // set search as first active tab
        binding.bottomNav.selectedItemId = R.id.searchFragment
    }
    // TODO: display errors in fragment somehow. (toast? put as bg text? snackbar?)

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // now that bottom nav has restored its state, we can set it up
        setupBottomNavBinding()
    }

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
            R.id.legalFragment -> NavigationUI.onNavDestinationSelected(item, currNavController?.value!!)
            else -> {
                Timber.e("Menu item not found")
                false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return currNavController?.value?.navigateUp() ?: false
    }

    override fun onBackPressed() {
        // attempt standard nav up (returns false when no fragments to pop off backstack)
        if (currNavController?.value?.navigateUp() != true) {
            // just minimize the app instead of finishing this activity
            val home = Intent(Intent.ACTION_MAIN)
            home.addCategory(Intent.CATEGORY_HOME)
            home.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(home)
        }
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
        val navGraphIds = listOf(
            R.navigation.search_nav_graph,
            R.navigation.now_playing_nav_graph,
            R.navigation.queue_nav_graph
        )

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = binding.bottomNav.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.party_nav_host_fragment,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer { navController ->
            setupActionBarWithNavController(this, navController)
        })
        currNavController = controller
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
        currNavController?.value?.navigate(R.id.partyCodeFragment)
    }

    private fun leaveParty() {
        // TODO:
    }
}
