package com.niehusst.partyq.ui.partyActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.ActivityPartyBinding
import kotlinx.android.synthetic.main.activity_party.*
import timber.log.Timber

class PartyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPartyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPartyBinding.inflate(layoutInflater)
        setupBottomNavBinding()
        startConnectionService()
        startSpotifyPlayerService()

        // set search as first active tab
        binding.bottomNav.selectedItemId = R.id.searchFragment

        setContentView(binding.root)
    }

    private fun setupBottomNavBinding() {
        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.searchFragment,
                R.id.queueFragment,
                R.id.nowPlayingFragment -> {
                    true
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

    private fun startConnectionService() {
        // TODO: delegate host vs guest logic to the repo/service
    }

    private fun startSpotifyPlayerService() {
        // TODO:
    }
}
