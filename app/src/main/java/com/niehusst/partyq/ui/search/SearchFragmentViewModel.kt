package com.niehusst.partyq.ui.search

import androidx.lifecycle.ViewModel

class SearchFragmentViewModel : ViewModel() {

    fun submitQuery(query: String?) {
        query ?: return

        // TODO: call on CommunicationService
    }
}