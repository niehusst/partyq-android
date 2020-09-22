package com.niehusst.partyq.ui.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.niehusst.partyq.databinding.QueueFragmentBinding

class QueueFragment : Fragment() {

    private lateinit var binding: QueueFragmentBinding

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
        // TODO: set binding.queueEmpty = queueService.isEmpty()
        binding.queueEmpty = true
        binding.songQueue.adapter = QueueAdapter()
    }
}
