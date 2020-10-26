package com.niehusst.partyq.ui.partyEnd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.niehusst.partyq.BundleNames
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.PartyEndFragmentBinding

class PartyEndFragment : Fragment() {

    private lateinit var binding: PartyEndFragmentBinding
    private lateinit var contentMessage: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PartyEndFragmentBinding.inflate(inflater)
        val default = requireContext().resources.getString(R.string.optional_end)
        contentMessage = arguments?.getString(BundleNames.END_MESSAGE, default) ?: default
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.endMessage = contentMessage
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.partyAgainBtn.setOnClickListener {
            findNavController().navigate(R.id.mainActivity)
            // prevent user from returning to ended party
            activity?.finish()
        }
    }
}
