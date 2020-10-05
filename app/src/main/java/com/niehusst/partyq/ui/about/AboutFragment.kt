package com.niehusst.partyq.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.niehusst.partyq.databinding.AboutFragmentBinding

class AboutFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return AboutFragmentBinding.inflate(layoutInflater).root
    }

    companion object {
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }
}
