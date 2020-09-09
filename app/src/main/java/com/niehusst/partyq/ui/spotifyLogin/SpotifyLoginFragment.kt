package com.niehusst.partyq.ui.spotifyLogin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.SpotifyLoginFragmentBinding
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse

class SpotifyLoginFragment : Fragment() {

    private lateinit var binding: SpotifyLoginFragmentBinding
    private val viewModel by viewModels<SpotifyLoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SpotifyLoginFragmentBinding.inflate(layoutInflater)
        binding.loading = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loading.observe(viewLifecycleOwner, Observer {
            binding.loading = it
        })
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.spotifyAuthButton.setOnClickListener {
            viewModel.connectToSpotify(activity as Activity)
        }

        binding.infoButton.setOnClickListener {
            findNavController().navigate(R.id.aboutFragment)
        }
    }

    fun onAuthResult(resultCode: Int, intent: Intent?) {
        val response = AuthenticationClient.getResponse(resultCode, intent)

        // TODO: save auth key somewhere
        when(response.type) {
            AuthenticationResponse.Type.TOKEN -> {
                // on success
                findNavController().navigate(R.id.partyActivity)
                // end the MainActivity so user can't go back to pre-login
                activity?.finish()
            }
            AuthenticationResponse.Type.ERROR -> {
                // on failure
                viewModel.stopLoading()
                // TODO: insert some sort of error remediation. Troubleshooting instructions?
                //  reiterate Requirements for party start?
                Toast.makeText(requireContext(), "Failed to connect to Spotify", Toast.LENGTH_LONG).show()
            }
            else -> {
                // auth flow was likely cancelled before completion
                viewModel.stopLoading()
                Toast.makeText(requireContext(), "Authentication cancelled", Toast.LENGTH_LONG).show()
            }
        }
    }
}
