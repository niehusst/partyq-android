package com.niehusst.partyq.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Global status of device disconnection from a party.
 * This state is required to be global so as to be visible to Nearby Connections API callbacks
 * that are triggered when connection is lost between host and guest.
 */
object PartyDisconnectionHandler {

    private val _disconnected = MutableLiveData(false)
    val disconnected: LiveData<Boolean> = _disconnected

    fun disconnectFromParty() {
        _disconnected.postValue(true)
    }

    fun acknowledgeDisconnect() {
        _disconnected.postValue(false)
    }
}
