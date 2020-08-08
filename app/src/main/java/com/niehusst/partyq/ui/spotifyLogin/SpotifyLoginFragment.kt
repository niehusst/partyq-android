package com.niehusst.partyq.ui.spotifyLogin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.niehusst.partyq.PartyqApplication
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.SpotifyLoginFragmentBinding
import com.niehusst.partyq.ui.partyActivity.PartyActivity

class SpotifyLoginFragment : Fragment() {

    private lateinit var binding: SpotifyLoginFragmentBinding
    private val viewModel by viewModels<SpotifyLoginViewModel> {
        SpotifyLoginViewModel.SpotifyLoginViewModelFactory(
            (requireContext().applicationContext as PartyqApplication).spotifyAuthRepository)
    }

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
            viewModel.connectToSpotify(requireContext(), {
                // on success
                // TODO: Add some loading wheel for wait time?
                launchPartyActivity()
                // end the MainActivity so user can't go back to pre-login
                activity?.finish()
            }, {
                // on failure
                // TODO: insert some sort of error remediation. Troubleshooting instructions?
                //  Requirements for party start?
                Toast.makeText(requireContext(), "Failed to connect to Spotify", Toast.LENGTH_LONG).show()
            })
        }

        binding.infoButton.setOnClickListener {
            view.findNavController().navigate(R.id.aboutFragment)
        }
    }

    fun launchPartyActivity() {
        val intent = Intent(requireContext(), PartyActivity::class.java)
        startActivity(intent)
    }
}
