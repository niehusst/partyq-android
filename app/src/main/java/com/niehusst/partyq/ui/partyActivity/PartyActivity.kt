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

package com.niehusst.partyq.ui.partyActivity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.CallSuper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.niehusst.partyq.BundleNames
import com.niehusst.partyq.R
import com.niehusst.partyq.SharedPrefNames.PARTY_FIRST_START
import com.niehusst.partyq.SharedPrefNames.PREFS_FILE_NAME
import com.niehusst.partyq.databinding.ActivityPartyBinding
import com.niehusst.partyq.extensions.setupWithNavController
import com.niehusst.partyq.ui.remediation.RemediationActivity
import com.niehusst.partyq.services.*
import com.niehusst.partyq.services.CommunicationService.REQUEST_CODE_REQUIRED_PERMISSIONS
import com.niehusst.partyq.services.CommunicationService.REQUIRED_PERMISSIONS
import com.niehusst.partyq.ui.about.AboutFragment
import com.niehusst.partyq.ui.legal.LegalFragment
import com.niehusst.partyq.ui.support.SupportFragment
import timber.log.Timber

class PartyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPartyBinding
    private var currNavController: LiveData<NavController>? = null
    private val viewModel by viewModels<PartyActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_party)
        setSupportActionBar(findViewById(R.id.toolbar))
        startCommunicationService()
        startSpotifyPlayerService()
        listenForDisconnection()
        if (savedInstanceState == null) {
            setupBottomNavBinding()
        } // else wait for onRestoreInstanceState()

        // set search as first active tab
        binding.bottomNav.selectedItemId = R.id.searchFragment
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // now that bottom nav has restored its state, we can set it up
        setupBottomNavBinding()
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

        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS)
        }
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
            R.id.aboutFragment -> {
                AboutFragment.newInstance().show(supportFragmentManager, null)
                true
            }
            R.id.legalFragment -> {
                LegalFragment.newInstance().show(supportFragmentManager, null)
                true
            }
            R.id.supportFragment -> {
                SupportFragment.newInstance().show(supportFragmentManager, null)
                true
            }
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

    private fun startCommunicationService() {
        viewModel.startCommunicationService(this)
    }

    private fun startSpotifyPlayerService() {
        viewModel.startSpotifyPlayerService(this)

        assertHostHasSpotifyPremium() // partyq wont work without Spotify premium
    }

    private fun assertHostHasSpotifyPremium() {
        SpotifyPlayerService.fullyInit.observe(this, Observer {
            if (it && SpotifyPlayerService.userHasSpotifyPremium == false) {
                Timber.e("Spotify premium not detected")
                launchRemediationActivity(R.string.no_spotify_premium_msg)
                // do not let user return to dysfunctional party state
                finish()
            }
        })
    }

    private fun launchPartyCodeDialog() {
        currNavController?.value?.navigate(R.id.partyCodeFragment)
    }

    private fun leaveParty() {
        // build confirmation modal
        val builder = AlertDialog.Builder(this)
        if (UserTypeService.isHost(this)) {
            builder.setMessage(R.string.leave_confirmation_msg_host)
        } else {
            builder.setMessage(R.string.leave_confirmation_msg_guest)
        }
        builder.apply {
            setTitle(R.string.leave_party)
            setPositiveButton(R.string.leave) { dialog, _ ->
                disconnect(forced = false)
                dialog.dismiss()
            }
            setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }

        builder.create().show()
    }

    /**
     * Disconnect from and clear all partyq services, and launch the PartyEndActivity.
     *
     * @param forced - Indicates whether the host forcefully disconnected from the guest (true), or
     *                  the guest voluntarily left the party (false). Indicates which text to show.
     */
    private fun disconnect(forced: Boolean) {
        Timber.i("Disconnected from party. By force: $forced")
        // clean up party state
        viewModel.resetAllServices(this)

        // choose correct message to display in PartyEndActivity and launch, preventing return
        val bundle = if (forced) {
            bundleOf(BundleNames.END_MESSAGE to resources.getString(R.string.forced_end))
        } else {
            bundleOf(BundleNames.END_MESSAGE to resources.getString(R.string.optional_end))
        }
        findNavController(R.id.party_nav_host_fragment).navigate(R.id.partyEndActivity, bundle)
        finish()
    }

    private fun listenForDisconnection() {
        PartyDisconnectionHandler.disconnected.observe(this, Observer { disconnected ->
            if (disconnected) {
                PartyDisconnectionHandler.acknowledgeDisconnect()
                disconnect(forced = true)
            }
        })
    }

    /**
     * Returns true if the app was granted all the permissions. Otherwise, returns false.
     */
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                Timber.e("Required permission $permission was denied")
                return false
            }
        }
        return true
    }

    /**
     * Handles user acceptance (or denial) of our permission request.
     */
    @CallSuper
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_CODE_REQUIRED_PERMISSIONS) {
            return
        }
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                launchRemediationActivity(R.string.denied_permissions_msg)
                finish()
                return
            }
        }
        recreate()
    }

    private fun launchRemediationActivity(strId: Int) {
        val intent = Intent(this, RemediationActivity::class.java)
        intent.putExtra(
            BundleNames.REMEDIATION_MESSAGE,
            resources?.getString(strId)
        )
        startActivity(intent)
    }
}
