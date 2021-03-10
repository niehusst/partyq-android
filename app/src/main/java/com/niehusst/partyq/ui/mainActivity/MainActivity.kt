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

package com.niehusst.partyq.ui.mainActivity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.niehusst.partyq.BundleNames
import com.niehusst.partyq.R
import com.niehusst.partyq.SharedPrefNames.AGREED_TO_EULA
import com.niehusst.partyq.SharedPrefNames.PREFS_FILE_NAME
import com.niehusst.partyq.SpotifySharedInfo
import com.niehusst.partyq.services.CommunicationService
import com.niehusst.partyq.ui.about.AboutFragment
import com.niehusst.partyq.ui.legal.LegalFragment
import com.niehusst.partyq.ui.support.SupportFragment
import com.niehusst.partyq.ui.remediation.RemediationActivity
import com.niehusst.partyq.ui.spotifyLogin.SpotifyLoginFragment
import com.niehusst.partyq.utility.EulaDialog
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

        // set Firebase key to indicate a user's host status
        FirebaseCrashlytics.getInstance().setCustomKey("isHost", false)
    }

    override fun onStart() {
        super.onStart()
        val prefs = getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
        // prompt user for EULA agreement if they've never agreed before
        if (!prefs.getBoolean(AGREED_TO_EULA, false)) {
            EulaDialog.showDialog(this)
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SpotifySharedInfo.REQUEST_CODE) {
            // let the visible instance of SpotifyLoginFragment handle the rest of auth req
            val fragment = supportFragmentManager.fragments.firstOrNull()
                ?.childFragmentManager?.fragments?.firstOrNull { it.isVisible }
            (fragment as? SpotifyLoginFragment)?.onAuthResult(resultCode, data)
        }
    }

    /**
     * Handles user acceptance (or denial) of our permission request.
     * (Requested in child fragment PartyJoinFragment, but result is sent to parent activity)
     */
    @CallSuper
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != CommunicationService.REQUEST_CODE_REQUIRED_PERMISSIONS) {
            return
        }
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                launchRemediationActivity()
                finish()
                return
            }
        }
        recreate()
    }

    private fun launchRemediationActivity() {
        val intent = Intent(this, RemediationActivity::class.java)
        intent.putExtra(
            BundleNames.REMEDIATION_MESSAGE,
            resources?.getString(R.string.denied_permissions_msg)
        )
        startActivity(intent)
    }
}
