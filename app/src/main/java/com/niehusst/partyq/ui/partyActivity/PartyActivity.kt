package com.niehusst.partyq.ui.partyActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.ActivityPartyBinding
import com.niehusst.partyq.services.CommunicationService
import timber.log.Timber

class PartyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPartyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_party)
        initServicePrereqs()
        setupBottomNavBinding()
        startConnectionService()
        startSpotifyPlayerService()

        // set search as first active tab
        binding.bottomNav.selectedItemId = R.id.searchFragment
    }
//TODO: launch partycode dialog fragment onStart if hasnt been launched before
// (save the party code into SharedPrefs to tell if this specific party has had the dialog launched yet
//  (if no saved code or code doesnt match curr code, then launch frag))
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

    private fun initServicePrereqs() {
    }

    private fun startConnectionService() {
        // TODO: delegate host vs guest logic to the repo/service
    }

    private fun startSpotifyPlayerService() {
        // TODO:
    }
}
