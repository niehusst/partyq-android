package com.niehusst.partyq.ui.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.niehusst.partyq.databinding.SupportFragmentBinding

class SupportFragment : BottomSheetDialogFragment() {

    private lateinit var binding: SupportFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SupportFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // create links
        binding.issuesLink.movementMethod = LinkMovementMethod()
        binding.faqLink.movementMethod = LinkMovementMethod()

        binding.emailButton.setOnClickListener {
            launchEmailIntent()
        }
    }

    private fun launchEmailIntent() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf("partyqhelp@gmail.com"))
            data = Uri.parse("mailto:")
        }

        if (emailIntent.resolveActivity(requireContext().packageManager) == null) {
            Toast.makeText(requireContext(), "No email app found", Toast.LENGTH_LONG).show()
        } else {
            startActivity(Intent.createChooser(emailIntent, "Choose an email app:"))
        }
    }

    companion object {
        fun newInstance(): SupportFragment {
            return SupportFragment()
        }
    }
}
