package com.niehusst.partyq.ui.legal

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.niehusst.partyq.databinding.LegalFragmentBinding

class LegalFragment : BottomSheetDialogFragment() {

    private lateinit var binding: LegalFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LegalFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // link github URL
        binding.partyqLicText.movementMethod = LinkMovementMethod.getInstance()
    }

    companion object {
        fun newInstance(): LegalFragment {
            return LegalFragment()
        }
    }
}
