package com.niehusst.partyq.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

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