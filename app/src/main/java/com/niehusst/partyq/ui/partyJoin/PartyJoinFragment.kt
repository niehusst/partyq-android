package com.niehusst.partyq.ui.partyJoin

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.PartyJoinFragmentBinding
import com.niehusst.partyq.network.Status
import com.niehusst.partyq.services.CommunicationService
import com.niehusst.partyq.services.PartyCodeHandler
import timber.log.Timber

class PartyJoinFragment : Fragment() {

    private lateinit var binding: PartyJoinFragmentBinding
    private val viewModel by viewModels<PartyJoinViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PartyJoinFragmentBinding.inflate(inflater)
        // start comms service so guests can use it to connect to host
        CommunicationService.start(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loading = false

        observeConnectionStatus()

        binding.submitButton.setOnClickListener {
            // TODO: will closing the app while in discovery mode cause mem leaks?
            val codeLongEnough = viewModel.connectToParty(binding.codeEditText.text.toString())

            if (!codeLongEnough) {
                Toast.makeText(
                    requireContext(),
                    R.string.party_code_too_short_message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!hasPermissions(requireContext(), CommunicationService.REQUIRED_PERMISSIONS)) {
            activity?.requestPermissions(
                CommunicationService.REQUIRED_PERMISSIONS,
                CommunicationService.REQUEST_CODE_REQUIRED_PERMISSIONS
            )
        }
    }

    private fun observeConnectionStatus() {
        CommunicationService.connected.observe(viewLifecycleOwner, Observer { status ->
            when(status) {
                Status.SUCCESS -> {
                    Timber.i("Successfully connected to a party")
                    // save the party code that got us connected
                    PartyCodeHandler.setPartyCode(viewModel.lastCode, requireContext())

                    // nav to party and end MainActivity so user can't go back to pre-login
                    findNavController().navigate(R.id.partyActivity)
                    activity?.finish()
                }
                Status.LOADING -> binding.loading = true
                Status.ERROR -> {
                    // TODO: nav to remediation
                    Timber.e("Error trying to connect to party ${viewModel.lastCode}")
                    Toast.makeText(requireContext(), "Something hecked up", Toast.LENGTH_LONG).show()
                    binding.loading = false
                }
                else -> { /* do nothing, no attempt to connect yet */ }
            }
        })
    }

    /**
     * Returns true if the app was granted all the permissions. Otherwise, returns false.
     */
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * Handles user acceptance (or denial) of our permission request.
     */
    @CallSuper
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != CommunicationService.REQUEST_CODE_REQUIRED_PERMISSIONS) {
            return
        }
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                // TODO: nav to remediation activity
                Toast.makeText(requireContext(), "Partyq cannot function without these permissions", Toast.LENGTH_LONG).show()
                activity?.finish()
                return
            }
        }
        activity?.recreate()
    }
}
