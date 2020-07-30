package com.niehusst.partyq.ui.spotifyLogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.niehusst.partyq.databinding.SpotifyLoginFragmentBinding
import com.niehusst.partyq.services.SpotifyAuthenticationService
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
            connectToSpotfiy()
        }

        binding.infoButton.setOnClickListener {
            // TODO: navigate to about page
            Toast.makeText(requireContext(), "Not yet implemented", Toast.LENGTH_SHORT).show()
        }
    }

    // TODO: this should go in its own service, which should be accessed in the viewModel
    private val clientId = SpotifyAuthenticationService.getSpotifyKey()
    private val redirectUri = "com.niehusst.partyq://callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private fun connectToSpotfiy() {
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(requireContext(), connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Timber.d("Connected to Spotify!")
                // Now you can start interacting with App Remote
                // TODO: save connection info for PartyActivity
                Toast.makeText(requireContext(), "Connect success", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(throwable: Throwable) {
                Timber.e(throwable)
                // Something went wrong when attempting to connect
                Toast.makeText(requireContext(), "Error connecting to Spotify app", Toast.LENGTH_LONG).show()
            }
        })
    }
}