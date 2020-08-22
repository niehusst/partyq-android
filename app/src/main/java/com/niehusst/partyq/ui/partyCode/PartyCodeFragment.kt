package com.niehusst.partyq.ui.partyCode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.PartyCodeFragmentBinding
import com.niehusst.partyq.databinding.PartyConnectFragmentBinding
import timber.log.Timber

class PartyCodeFragment : Fragment() {

    private lateinit var binding: PartyCodeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PartyCodeFragmentBinding.inflate(layoutInflater)
        return binding.root
    }
}
