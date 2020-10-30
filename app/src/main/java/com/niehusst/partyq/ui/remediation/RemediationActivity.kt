package com.niehusst.partyq.ui.remediation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.niehusst.partyq.BundleNames
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.ActivityRemediationBinding
import com.niehusst.partyq.ui.mainActivity.MainActivity

class RemediationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRemediationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_remediation)
        val default = resources.getString(R.string.generic_error_msg)
        binding.remediationMsg = intent.getStringExtra(BundleNames.REMEDIATION_MESSAGE) ?: default

        binding.confirmBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            // prevent returning to error screen
            finish()
        }
    }
}
