package com.niehusst.partyq.ui.partyJoin

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.PartyJoinFragmentBinding
import com.niehusst.partyq.network.Status
import com.niehusst.partyq.services.CommunicationService
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
        viewModel.startCommunicationService(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loading = false

        observeConnectionStatus()
        setClickListeners()
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

    override fun onStop() {
        super.onStop()
        // stop discovery, if it hasn't already stopped by this point
        viewModel.stopSearchingForParty()
    }

    private fun setClickListeners() {
        binding.submitButton.setOnClickListener {
            val codeLongEnough = viewModel.searchForParty(binding.codeEditText.text.toString())

            if (!codeLongEnough) {
                Toast.makeText(
                    requireContext(),
                    R.string.party_code_too_short_message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.stopButton.setOnClickListener {
            viewModel.stopSearchingForParty()
        }
    }

    private fun observeConnectionStatus() {
        CommunicationService.connected.observe(viewLifecycleOwner, Observer { status ->
            when(status) {
                Status.SUCCESS -> {
                    Timber.d("Successfully connected to a party")
                    viewModel.setGuestData(requireContext())

                    // nav to party and end MainActivity so user can't go back to pre-login
                    findNavController().navigate(R.id.partyActivity)
                    activity?.finish()
                }
                Status.LOADING -> binding.loading = true
                Status.ERROR -> {
                    Timber.e("Error trying to connect to party ${viewModel.lastCode}")

                    val snackPopup = Snackbar.make(
                        binding.root,
                        R.string.party_not_found,
                        Snackbar.LENGTH_LONG
                    )
                    snackPopup.view.setBackgroundColor(binding.root.context.getColor(R.color.colorError))
                    val layoutParams = snackPopup.view.layoutParams as FrameLayout.LayoutParams
                    layoutParams.gravity = Gravity.TOP
                    layoutParams.topMargin = requireContext().resources.getDimension(R.dimen.toolBarHeightBuffered).toInt()
                    snackPopup.view.layoutParams = layoutParams
                    snackPopup.setTextColor(binding.root.context.getColor(R.color.onColorError))
                    snackPopup.show()

                    binding.loading = false
                }
                else -> {
                    // for null/NO_ACTION just stop loading and wait for user interaction
                    binding.loading = false
                }
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
}
