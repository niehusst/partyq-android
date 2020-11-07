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

    fun resetAllServices() {
        CommunicationService.disconnectFromParty()
        SpotifyPlayerService.disconnect()
        SkipSongHandler.clearSkipCount()
        SearchResultHandler.clearSearch()
        QueueService.clearQueue()
    }
}