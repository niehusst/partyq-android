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
