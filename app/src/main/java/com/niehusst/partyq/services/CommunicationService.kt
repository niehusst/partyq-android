package com.niehusst.partyq.services

import com.niehusst.partyq.network.models.Item

object CommunicationService { // TODO: think about making this into a bound service

    fun connectToParty() {
        // TODO: start/connect to party net
    }

    fun sendSearchRequest(query: String) {
        // TODO: send/get search req
    }

    fun addToQueue(item: Item) {
        // TODO: update queue
    }

    fun disconnectFromParty() {
        // TODO: send disconnect message to all connections, then disconnect from them
    }
}
