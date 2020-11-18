/*
 * Copyright 2020 Liam Niehus-Staab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.niehusst.partyq.ui.partyJoin

import android.content.Context
import androidx.lifecycle.ViewModel
import com.niehusst.partyq.services.CommunicationService
import com.niehusst.partyq.services.PartyCodeHandler
import com.niehusst.partyq.services.UserTypeService

class PartyJoinViewModel : ViewModel() {

    var lastCode: String = ""

    fun startCommunicationService(context: Context) {
        CommunicationService.start(context)
    }

    /**
     * Begins connection discovery.
     *
     * @return - false to indicate problem with code length, else true when discovery has started
     */
    fun searchForParty(code: String): Boolean {
        if (code.length != 4) {
            return false
        }
        lastCode = code
        CommunicationService.connectToParty(code)
        return true
    }

    fun stopSearchingForParty() {
        CommunicationService.stopSearchingForParty()
    }

    fun setGuestData(context: Context) {
        // save the code that got us connected
        PartyCodeHandler.setPartyCode(lastCode, context)

        UserTypeService.setSelfAsGuest(context)
    }
}
