package com.niehusst.partyq.ui.partyEnd

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.niehusst.partyq.BundleNames
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.ActivityPartyEndBinding
import com.niehusst.partyq.ui.mainActivity.MainActivity

class PartyEndActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPartyEndBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_party_end)

        val default = resources.getString(R.string.optional_end)
        binding.endMessage = intent.getStringExtra(BundleNames.END_MESSAGE) ?: default
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.partyAgainBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // prevent user from returning to ended party
            finish()
        }
    }
}
