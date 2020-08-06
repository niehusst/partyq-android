package com.niehusst.partyq.ui.partyActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.ActivityPartyBinding
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class PartyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPartyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPartyBinding.inflate(layoutInflater)
        setupBottomNavBinding()
    }

    private fun setupBottomNavBinding() {
        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.searchFragment,
                R.id.queueFragment,
                R.id.nowPlayingFragment -> {
                    NavigationUI.onNavDestinationSelected(item, nav_host_fragment.findNavController())
                }
                else -> {
                    Timber.e("Bottom Nav selection ${item.title} not recognized")
                    false
                }
            }
        }
    }
}