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

package com.niehusst.partyq.ui.spotifyLogin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.niehusst.partyq.BundleNames
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.SpotifyLoginFragmentBinding
import com.niehusst.partyq.repository.SpotifyAuthRepository
import com.niehusst.partyq.ui.remediation.RemediationActivity
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse
import timber.log.Timber

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

        setObservers()
        setClickListeners()
    }

    private fun setObservers() {
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            binding.loading = it
        })

        viewModel.tokenStatus.observe(viewLifecycleOwner, Observer {
            if (it == SpotifyLoginViewModel.TokenStatus.RESULT) {
                if (viewModel.tokenResult != null) {
                    // save the OAuth token
                    viewModel.saveTokens(viewModel.tokenResult!!)

                    // create the party code and set self as host
                    viewModel.setSelfAsHost(requireContext())

                    launchPartyActivity()
                } else {
                    // oops something went wrong swapping code for tokens
                    viewModel.stopLoading()
                    Toast.makeText(
                        requireContext(),
                        R.string.auth_error,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun setClickListeners() {
        binding.spotifyAuthButton.setOnClickListener {
            viewModel.connectToSpotify(activity as Activity)
        }

        binding.infoButton.setOnClickListener {
            findNavController().navigate(R.id.aboutFragment)
        }
    }

    /**
     * Handles the activity result from the Spotify-provided LoginActivity. If successful,
     * saves the authentication token, create the code for the party, and sets the user
     * as the host.
     * Otherwise, the loading spinner is stopped and user is sent to RemediationActivity.
     */
    fun onAuthResult(resultCode: Int, intent: Intent?) {
        val response = AuthenticationClient.getResponse(resultCode, intent)
        SpotifyAuthRepository.start()

        when (response.type) {
            AuthenticationResponse.Type.CODE -> {
                // tell crashlytics this user is now a host
                FirebaseCrashlytics.getInstance().setCustomKey("isHost", true)

                viewModel.swapCodeForTokenAsync(response.code)
                // result handled in `setObservers` from observing `tokenResponse` live data
            }
            AuthenticationResponse.Type.ERROR -> {
                // on failure
                Timber.e("Auth error: ${response.error}")
                viewModel.stopLoading()
                if (response.error == "NO_INTERNET_CONNECTION") {
                    Toast.makeText(
                        requireContext(),
                        R.string.no_wifi_msg,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    launchRemediationActivity()
                }
            }
            else -> {
                Timber.e("Auth for type ${response.type} error: ${response.error}")
                // auth flow was likely cancelled before completion
                viewModel.stopLoading()
                Toast.makeText(
                    requireContext(),
                    R.string.auth_cancelled,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun launchPartyActivity() {
        findNavController().navigate(R.id.partyActivity)
        // end the MainActivity so user can't go back to pre-login
        activity?.finish()
    }

    private fun launchRemediationActivity() {
        val intent = Intent(activity, RemediationActivity::class.java)
        intent.putExtra(
            BundleNames.REMEDIATION_MESSAGE,
            context?.resources?.getString(R.string.generic_error_msg)
        )
        startActivity(intent)
        activity?.finish()
    }
}
