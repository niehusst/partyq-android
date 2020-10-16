package com.niehusst.partyq.ui.partyJoin

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
import com.niehusst.partyq.databinding.PartyJoinFragmentBinding
import com.niehusst.partyq.network.Status
import com.niehusst.partyq.services.CommunicationService

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

        setupObservers()

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

    private fun setupObservers() {
        CommunicationService.connected.observe(viewLifecycleOwner, Observer { status ->
            when(status) {
                Status.SUCCESS -> {
                    // TODO: save the party code that got us connected
                    findNavController().navigate(R.id.partyActivity)

                    // finally, end the MainActivity so user can't go back to pre-login
                    activity?.finish()
                }
                Status.LOADING -> {
                    binding.loading = true
                }
                Status.ERROR -> {
                    // TODO: nav to remediation
                }
                else -> { /* do nothing, no attempt to connect yet */ }
            }
        })
    }
}
