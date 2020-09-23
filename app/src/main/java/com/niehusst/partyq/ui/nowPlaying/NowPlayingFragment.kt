package com.niehusst.partyq.ui.nowPlaying

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.niehusst.partyq.databinding.NowPlayingFragmentBinding
import com.niehusst.partyq.network.models.Item
import com.niehusst.partyq.services.QueueService
import com.niehusst.partyq.services.UserTypeService

class NowPlayingFragment : Fragment() {

    private lateinit var binding: NowPlayingFragmentBinding
    private var currItem: Item? = null

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
        binding.isHost = UserTypeService.isHost(requireContext())
        currItem = QueueService.peekQueue()
        bindItem(currItem)

        QueueService.dataChangedTrigger.observe(viewLifecycleOwner, Observer {
            val newHead = QueueService.peekQueue()
            if (newHead != currItem) {
                currItem = newHead
                bindItem(currItem)
            }
        })
    }

    private fun bindItem(item: Item?) {
        item?.also {
            binding.songName = it.name
            binding.songArtist = it.artistsAsPrettyString()
            binding.songLink = getSpotifyLink(it)
            // Get first image since it should be the largest (last is smallest)
            Glide.with(this)
                .load(item.album.images?.firstOrNull()?.url)
                .fitCenter()
                .into(binding.albumImage)
        }
    }

    private fun getSpotifyLink(item: Item): String? {
        return listOf(
            item.externalUrls?.spotify,
            item.album.externalUrls?.spotify,
            item.artists?.firstOrNull()?.externalUrls?.spotify,
            item.album.artists?.firstOrNull()?.externalUrls?.spotify,
            item.href // last ditch effort to put out some spotify url
        ).firstOrNull { it != null }
    }
}
