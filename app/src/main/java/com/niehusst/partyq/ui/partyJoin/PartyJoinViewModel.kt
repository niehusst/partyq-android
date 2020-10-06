package com.niehusst.partyq.ui.partyJoin

import androidx.lifecycle.ViewModel

class PartyJoinViewModel : ViewModel() {

    fun connectToParty(code: String) {
        if (code.length != 4) {
            return // fast fail
        }
        // TODO:
    }
}
