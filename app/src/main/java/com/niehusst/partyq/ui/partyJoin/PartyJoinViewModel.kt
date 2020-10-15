package com.niehusst.partyq.ui.partyJoin

import androidx.lifecycle.ViewModel
import com.niehusst.partyq.services.CommunicationService

class PartyJoinViewModel : ViewModel() {

    fun connectToParty(code: String) {
        if (code.length != 4) {
            // TODO: indicate problem?
            return
        }
        CommunicationService.connectToParty(code)
    }
}
