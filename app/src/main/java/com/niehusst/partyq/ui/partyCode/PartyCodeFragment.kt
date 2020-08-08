package com.niehusst.partyq.ui.partyCode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.PartyConnectFragmentBinding

class PartyCodeFragment : Fragment() {

    private lateinit var binding: PartyConnectFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PartyConnectFragmentBinding.inflate(layoutInflater)
        return binding.root
    }
}