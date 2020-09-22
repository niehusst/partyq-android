package com.niehusst.partyq.ui.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.niehusst.partyq.databinding.QueueFragmentBinding
import com.niehusst.partyq.services.QueueService

class QueueFragment : Fragment() {

    private lateinit var binding: QueueFragmentBinding
    private val adapter = QueueAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = QueueFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.queueEmpty = adapter.queueCopy.isEmpty()
        binding.songQueue.adapter = adapter

        observeQueueData()
    }

    private fun observeQueueData() {
        QueueService.dataChangedTrigger.observe(viewLifecycleOwner, Observer {
            adapter.queueCopy = QueueService.getQueueItems()
            binding.queueEmpty = adapter.queueCopy.isEmpty()
            adapter.notifyDataSetChanged()
        })
    }
}
