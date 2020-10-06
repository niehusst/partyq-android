package com.niehusst.partyq.ui.partyJoin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.niehusst.partyq.databinding.PartyJoinFragmentBinding

class PartyJoinFragment : Fragment() {

    private lateinit var binding: PartyJoinFragmentBinding
    private val viewModel by viewModels<PartyJoinViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PartyJoinFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loading = false

        binding.submitButton.setOnClickListener {
            // TODO: handle loading
            viewModel.connectToParty(binding.codeEditText.text.toString())
        }
    }
}
