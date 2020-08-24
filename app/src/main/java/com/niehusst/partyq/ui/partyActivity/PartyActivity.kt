package com.niehusst.partyq.ui.partyActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.ActivityPartyBinding
import timber.log.Timber

class PartyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPartyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPartyBinding.inflate(layoutInflater)
        setupBottomNavBinding()
        startConnectionService()
        startSpotifyPlayerService()

        setContentView(binding.root)
    }

    private fun setupBottomNavBinding() {
        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.searchFragment,
                R.id.queueFragment,
                R.id.nowPlayingFragment -> {
                    true
//                    NavigationUI.onNavDestinationSelected(item, nav_host_fragment.findNavController())
                }
                else -> {
                    Timber.e("Bottom Nav selection ${item.title} not recognized")
                    false
                }
            }
        }
    }

    private fun startConnectionService() {
        // TODO:
    }

    private fun startSpotifyPlayerService() {
        // TODO:
    }
}
