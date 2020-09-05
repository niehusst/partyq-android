package com.niehusst.partyq.ui.spotifyLogin

import android.os.Bundle
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
            viewModel.connectToSpotify(requireContext(), {
                // on success
                findNavController().navigate(R.id.partyActivity)
                // end the MainActivity so user can't go back to pre-login
                activity?.finish()
            }, {
                // on failure
                viewModel.stopLoading()
                // TODO: insert some sort of error remediation. Troubleshooting instructions?
                //  reiterate Requirements for party start?
                Toast.makeText(requireContext(), "Failed to connect to Spotify", Toast.LENGTH_LONG).show()
            })
        }

        binding.infoButton.setOnClickListener {
            findNavController().navigate(R.id.aboutFragment)
        }
    }
}
