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

package com.niehusst.partyq.ui.nowPlaying

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.niehusst.partyq.R
import com.niehusst.partyq.databinding.NowPlayingFragmentBinding
import com.niehusst.partyq.network.models.api.Item
import com.niehusst.partyq.services.QueueService
import com.niehusst.partyq.services.UserTypeService

class NowPlayingFragment : Fragment() {

    private lateinit var binding: NowPlayingFragmentBinding
    private val viewModel by viewModels<NowPlayingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = NowPlayingFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        binding.isHost = UserTypeService.isHost(requireContext())
        viewModel.currItem = QueueService.peekQueue()
        bindItem(viewModel.currItem)

        QueueService.dataChangedTrigger.observe(viewLifecycleOwner, Observer {
            if (viewModel.isNewCurrItem()) {
                bindItem(viewModel.currItem)
                // new song is playing, so set playToggleButton to correct icon
                setDrawableToPause()
            }
        })
    }

    private fun bindItem(itemToBind: Item?) {
        itemToBind?.also { item ->
            binding.songName = item.name
            binding.songArtist = item.artistsAsPrettyString()

            // Get first image since it should be the largest (last is smallest)
            Glide.with(this)
                .load(item.album.images?.firstOrNull()?.url)
                .error(R.drawable.album)
                .placeholder(R.drawable.album)
                .fitCenter()
                .into(binding.albumImage)

            // set spotify link for this item
            binding.openSpotifyButton.setOnClickListener {
                openSpotifyFromLink(item.getSpotifyLink())
            }
        }
        binding.noSong = itemToBind == null
    }

    private fun setClickListeners() {
        binding.playToggleButton.setOnClickListener {
            if (binding.playToggleButton.tag == "play") {
                viewModel.playSong()
                // update the layout w/ content for pausing the song
                setDrawableToPause()
            } else { // tag == pause
                viewModel.pauseSong()
                // update the layout w/ content for playing the song
                setDrawableToPlay()
            }
        }
        binding.skipButton.setOnClickListener {
            viewModel.voteSkipSong(requireContext())
        }
    }

    private fun setDrawableToPause() {
        binding.playToggleButton.setImageDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_pause_24)
        )
        binding.playToggleButton.tag = "pause"
        binding.playToggleButton.contentDescription = getString(R.string.pause_song)
    }

    private fun setDrawableToPlay() {
        binding.playToggleButton.setImageDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_play_arrow_24)
        )
        binding.playToggleButton.tag = "play"
        binding.playToggleButton.contentDescription = getString(R.string.play_song)
    }

    private fun openSpotifyFromLink(linkUri: String) {
        val linkIntent = Intent(Intent.ACTION_VIEW).apply {
            data = if (UserTypeService.isHost(requireContext())) {
                // deeplink open spotify
                Uri.parse(linkUri)
            } else {
                // link to spotify app store listing
                Uri.parse(getString(R.string.spotify_app_store_link))
            }

            // attribute partyq as the referrer
            putExtra(
                Intent.EXTRA_REFERRER,
                Uri.parse("android-app://" + binding.root.context.packageName)
            )
        }

        startActivity(linkIntent)
    }
}
