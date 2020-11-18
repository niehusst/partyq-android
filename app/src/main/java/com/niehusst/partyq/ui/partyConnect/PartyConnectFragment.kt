/*
 * Copyright 2020 Liam Niehus-Staab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.niehusst.partyq.ui.partyConnect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.PartyConnectFragmentBinding

/**
 * Fragment to start the flow of of connecting a user to an existing party,
 * or starting their own.
 */
class PartyConnectFragment : Fragment() {

    private lateinit var binding: PartyConnectFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PartyConnectFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.partyJoinButton.setOnClickListener {
            this.findNavController().navigate(R.id.partyJoinFragment)
        }
        binding.partyStartButton.setOnClickListener {
            this.findNavController().navigate(R.id.spotifyLoginFragment)
        }
    }
}
