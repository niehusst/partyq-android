package com.niehusst.partyq.ui.partyCode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.niehusst.partyq.databinding.PartyCodeFragmentBinding
import com.niehusst.partyq.services.CommunicationService

class PartyCodeFragment : DialogFragment() {

    private lateinit var binding: PartyCodeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PartyCodeFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.code = CommunicationService.getPartyCode(requireContext())
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.closeButton.setOnClickListener {
            this.dismiss()
        }
    }

    companion object {
        fun newInstance(): PartyCodeFragment {
            return PartyCodeFragment()
        }
    }
}
