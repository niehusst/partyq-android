package com.niehusst.partyq.ui.partyJoin

import androidx.lifecycle.ViewModel
import com.niehusst.partyq.services.CommunicationService

class PartyJoinViewModel : ViewModel() {

    /**
     * Return false to indicate problem
     */
    fun connectToParty(code: String): Boolean {
        if (code.length != 4) {
            return false
        }
        CommunicationService.connectToParty(code)
        return true
    }
}
