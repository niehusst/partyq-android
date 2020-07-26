package com.niehusst.partyq.ui.legal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.niehusst.partyq.databinding.LegalFragmentBinding

class LegalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LegalFragmentBinding.inflate(layoutInflater).root
    }
}