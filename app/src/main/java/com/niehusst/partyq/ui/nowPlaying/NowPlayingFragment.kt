package com.niehusst.partyq.ui.nowPlaying

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
import com.niehusst.partyq.network.models.Item
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
                viewModel.hasVotedSkip = false
            }
        })
    }

    private fun bindItem(item: Item?) {
        item?.also {
            binding.songName = it.name
            binding.songArtist = it.artistsAsPrettyString()
            binding.songLink = it.getSpotifyLink()
            // Get first image since it should be the largest (last is smallest)
            Glide.with(this)
                .load(item.album.images?.firstOrNull()?.url)
                .fitCenter()
                .into(binding.albumImage)
        }
        binding.noSong = item == null
    }

    private fun setClickListeners() {
        binding.playToggleButton.setOnClickListener {
            if (binding.playToggleButton.tag == "play") {
                viewModel.playSong()
                // update the layout
                binding.playToggleButton.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_pause_24)
                )
                binding.playToggleButton.tag = "pause"
            } else { // pause
                viewModel.pauseSong()
                // update the layout
                binding.playToggleButton.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_play_arrow_24)
                )
                binding.playToggleButton.tag = "play"
            }
        }
        binding.skipButton.setOnClickListener {
            viewModel.skipSong(requireContext())
        }
    }
}
