package com.niehusst.partyq.ui.partyJoin

import androidx.lifecycle.ViewModel
import com.niehusst.partyq.services.CommunicationService

class PartyJoinViewModel : ViewModel() {

    var lastCode: String = ""

    /**
     * Begins connection discovery.
     * Returns false to indicate problem with code length.
     */
    fun connectToParty(code: String): Boolean {
        if (code.length != 4) {
            return false
        }
        lastCode = code
        CommunicationService.connectToParty(code)
        return true
    }
}
