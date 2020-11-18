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

package com.niehusst.partyq.ui.partyActivity

import android.content.Context
import androidx.lifecycle.ViewModel
import com.niehusst.partyq.repository.SpotifyRepository
import com.niehusst.partyq.services.*
import timber.log.Timber

class PartyActivityViewModel : ViewModel() {

    fun startCommunicationService(context: Context) {
        CommunicationService.start(context)

        if (UserTypeService.isHost(context)) {
            PartyCodeHandler.getPartyCode(context)?.let { code ->
                Timber.d("Starting to advertise for $code")
                CommunicationService.hostAdvertise(code)
            }
        }
    }

    fun startSpotifyPlayerService(context: Context) {
        SpotifyRepository.start(context)
        SpotifyPlayerService.start(context, KeyFetchService.getSpotifyKey())
    }

    fun resetAllServices(context: Context) {
        CommunicationService.disconnectFromParty()
        SpotifyPlayerService.disconnect()
        SkipSongHandler.clearSkipCount()
        SearchResultHandler.clearSearch()
        QueueService.clearQueue()
        UserTypeService.clearHostData(context)
        SpotifyRepository.stop()
    }
}
