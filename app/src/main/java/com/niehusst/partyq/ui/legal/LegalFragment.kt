package com.niehusst.partyq.ui.legal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.niehusst.partyq.databinding.LegalFragmentBinding

class LegalFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LegalFragmentBinding.inflate(layoutInflater).root
    }

    companion object {
        fun newInstance(): LegalFragment {
            return LegalFragment()
        }
    }
}
