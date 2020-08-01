package com.niehusst.partyq.ui.spotifyLogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.SpotifyLoginFragmentBinding
import com.niehusst.partyq.services.KeyFetchService
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import timber.log.Timber

class SpotifyLoginFragment : Fragment() {

    private lateinit var binding: SpotifyLoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SpotifyLoginFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.spotifyAuthButton.setOnClickListener {
            // TODO: auth with spotify
            connectToSpotify()
        }

        binding.infoButton.setOnClickListener {
            view.findNavController().navigate(R.id.aboutFragment)
        }
    }

    // TODO: this should go in its own service, which should be accessed in the viewModel
    private fun connectToSpotify() {

    }
}