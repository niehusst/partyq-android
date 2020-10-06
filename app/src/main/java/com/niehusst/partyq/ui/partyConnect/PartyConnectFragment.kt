package com.niehusst.partyq.ui.partyConnect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.PartyConnectFragmentBinding

/**
 * Fragment to start the flow of of connecting a user to an existing party,
 * or starting their own.
 */
class PartyConnectFragment : Fragment() {

    private lateinit var binding: PartyConnectFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PartyConnectFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.partyJoinButton.setOnClickListener {
            this.findNavController().navigate(R.id.partyJoinFragment)
        }
        binding.partyStartButton.setOnClickListener {
            this.findNavController().navigate(R.id.spotifyLoginFragment)
        }
    }
}
