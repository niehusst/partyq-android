package com.niehusst.partyq.ui.about

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.niehusst.partyq.databinding.AboutFragmentBinding
import timber.log.Timber

class AboutFragment : BottomSheetDialogFragment() {

    private lateinit var binding: AboutFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AboutFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val ctx = requireContext()
            val pInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
            binding.versionNumber = "v" + pInfo.versionName
        } catch (ex: PackageManager.NameNotFoundException) {
            Timber.e(ex)
        }

        // link github URL in text
        binding.partyqLicense.movementMethod = LinkMovementMethod.getInstance()
    }

    companion object {
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }
}
