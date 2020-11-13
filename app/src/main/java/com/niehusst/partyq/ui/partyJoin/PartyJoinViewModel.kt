package com.niehusst.partyq.ui.partyJoin

import android.content.Context
import androidx.lifecycle.ViewModel
import com.niehusst.partyq.services.CommunicationService
import com.niehusst.partyq.services.PartyCodeHandler
import com.niehusst.partyq.services.UserTypeService

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

    fun setGuestData(context: Context) {
        // save the code that got us connected
        PartyCodeHandler.setPartyCode(lastCode, context)

        UserTypeService.setSelfAsGuest(context)
    }
}
