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

import android.Manifest
import android.content.Context
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.nearby.connection.Payload.Type.BYTES
import com.niehusst.partyq.network.Status
import com.niehusst.partyq.network.models.api.Item
import com.niehusst.partyq.network.models.api.SearchResult
import com.niehusst.partyq.network.models.connection.ConnectionPayload
import com.niehusst.partyq.utility.PayloadBuilder
import com.niehusst.partyq.utility.PayloadBuilder.buildEnqueuePayload
import com.niehusst.partyq.utility.PayloadBuilder.buildQueryPayload
import com.niehusst.partyq.utility.PayloadBuilder.buildSearchResultPayload
import com.niehusst.partyq.utility.PayloadBuilder.buildSkipVotePayload
import com.niehusst.partyq.utility.PayloadBuilder.buildUpdatedQueuePayload
import com.niehusst.partyq.network.models.connection.Type
import com.niehusst.partyq.repository.SpotifyRepository
import com.niehusst.partyq.utility.CompressionUtility.decompress
import com.niehusst.partyq.utility.PayloadBuilder.buildPagedSearchPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

object CommunicationService {

    private lateinit var connectionsClient: ConnectionsClient

    private val STRATEGY = Strategy.P2P_STAR
    private const val SERVICE_ID = "com.niehusst.partyq" // identifies this app for Nearby API connections
    const val REQUEST_CODE_REQUIRED_PERMISSIONS = 1
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.NFC,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private const val TIMEOUT_DISCOVERY_MILLIS = 20000L
    private const val TIMEOUT_INTERVAL_MILLIS = 1000L
    private var discoveryTimer: CountDownTimer? = null

    // list of IDs of party guests connected to the host device
    val connectionEndpointIds = mutableListOf<String>()

    // for communicating to guests when they've been connected to a party
    private val _connected = MutableLiveData(Status.NO_ACTION)
    val connected: LiveData<Status> = _connected

    /**
     * This function must be called before any other in order to initialize `connectionsClient`.
     * Required for both Host and Guest users.
     */
    fun start(context: Context) {
        connectionsClient = Nearby.getConnectionsClient(context)
    }

    /* Party connection methods and callbacks */

    /**
     * Broadcast the calling device's availability for connection via Nearby Connections API.
     * The device should continue advertising until `disconnectFromParty()` is called.
     * Host only method.
     */
    fun hostAdvertise(partyCode: String) {
        val advertOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startAdvertising(
            partyCode, // identifier for this party
            SERVICE_ID, // app identifier
            object : ConnectionLifecycleCallback() {
                override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                    // verify matching party code before accepting connection
                    if (connectionInfo.endpointName == partyCode) {
                        Timber.d("Accepting connection to $endpointId")
                        connectionsClient.acceptConnection(endpointId, payloadHandlerCallBacks)
                    } else {
                        Timber.d("Rejecting connection due to mismatched party code ${connectionInfo.endpointName}")
                        connectionsClient.rejectConnection(endpointId)
                    }
                }

                override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                    if (result.status.isSuccess) {
                        Timber.i("Nearby API successfully connected to $endpointId")
                        connectionEndpointIds.add(endpointId)
                        // give the newly connected endpoint the current queue
                        sendUpdatedQueue(QueueService.getQueueItems())

                        // don't stop advertising; keep connecting to guests until party ends
                    } else {
                        Timber.e("Nearby API advertising; an endpoint connection failed")
                    }
                }

                override fun onDisconnected(endpointId: String) {
                    connectionEndpointIds.remove(endpointId)
                }
            },
            advertOptions
        ).addOnFailureListener { Timber.e("Advertising failed to start: $it") }
            .addOnSuccessListener { Timber.d("Started advertising successfully") }
        // TODO: add on failure listener to stop app if we cant connect people to party??
    }

    /**
     * Allow guests to connect to an existing party with a party code matching `inputPartyCode`.
     * Communicates back whether a party was found or not matching `inputPartyCode` via the
     * `connected` LiveData.
     */
    fun connectToParty(inputPartyCode: String) {
        _connected.value = Status.LOADING

        val discoverOptions = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startDiscovery(
            SERVICE_ID, // app identifier
            object : EndpointDiscoveryCallback() {
                override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                    // double check verify that party codes match before attempting connection
                    if (info.endpointName == inputPartyCode) {
                        Timber.d("Attempting connection to $endpointId")
                        connectionsClient.requestConnection(
                            inputPartyCode,
                            endpointId,
                            buildGuestConnectionLifecycleCallback(inputPartyCode)
                        ).addOnSuccessListener { Timber.d("Connection requested successfully") }
                            .addOnFailureListener { Timber.e("Connection request failed: $it") }
                    }
                }

                override fun onEndpointLost(endpointId: String) {
                    Timber.e("Lost discovered endpoint $endpointId")
                    _connected.value = Status.ERROR
                }
            },
            discoverOptions
        ).addOnFailureListener { Timber.e("Failed to start discovery: $it") }
            .addOnSuccessListener { Timber.d("Discovery started successfully") }

        // timeout discovery so guest isn't left hanging if no host is found
        startDiscoveryTimer()
    }

    private fun buildGuestConnectionLifecycleCallback(inputPartyCode: String) =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                // only connect if the party codes are the same
                if (info.endpointName == inputPartyCode) {
                    connectionsClient.acceptConnection(endpointId, payloadHandlerCallBacks)
                        .addOnSuccessListener { Timber.d("Connection accepted successfully") }
                        .addOnFailureListener { Timber.e("Accepting connection failed: $it") }
                }
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                if (result.status.isSuccess) {
                    Timber.i("Nearby API successfully connected to $endpointId")
                    _connected.value = Status.SUCCESS
                    connectionEndpointIds.add(endpointId)
                    connectionsClient.stopDiscovery() // only connect to 1 host
                } else {
                    Timber.e("Nearby API advertising: endpoint $endpointId connection failed")
                    _connected.value = Status.ERROR
                    connectionsClient.stopDiscovery()
                }
            }

            override fun onDisconnected(endpointId: String) {
                connectionEndpointIds.remove(endpointId)
                // since the host has disconnected from us, we must leave the party
                PartyDisconnectionHandler.disconnectFromParty()
            }
        }


    private fun startDiscoveryTimer() {
        discoveryTimer = object : CountDownTimer(TIMEOUT_DISCOVERY_MILLIS, TIMEOUT_INTERVAL_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                if (_connected.value == Status.SUCCESS) {
                    // cancel count down on successful connection
                    this.cancel()
                }
            }

            override fun onFinish() {
                if (_connected.value == Status.LOADING) {
                    connectionsClient.stopDiscovery()
                    _connected.value = Status.ERROR
                }
            }
        }.start()
    }

    fun stopSearchingForParty() {
        connectionsClient.stopDiscovery()
        _connected.value = Status.NO_ACTION

        // stop count down timer
        discoveryTimer?.cancel()
        discoveryTimer = null
    }


    /* Data sending methods */

    /** Guest only method */
    fun sendQuery(query: String) {
        if (connectionEndpointIds.isNotEmpty()) {
            // send payload to host; the first and only endpoint in the list
            connectionsClient.sendPayload(
                connectionEndpointIds[0],
                buildQueryPayload(query)
            )
        }
    }

    /** Guest only method */
    fun sendPagedSearch(reqUrl: String) {
        if (connectionEndpointIds.isNotEmpty()) {
            // send payload to host; the first and only endpoint in the list
            connectionsClient.sendPayload(
                connectionEndpointIds[0],
                buildPagedSearchPayload(reqUrl)
            )
        }
    }

    /** Host only method */
    fun sendSearchResults(requestingEndpointId: String, results: SearchResult?) {
        connectionsClient.sendPayload(
            requestingEndpointId,
            buildSearchResultPayload(results)
        )
    }

    /** Guest only method */
    fun sendEnqueueRequest(item: Item) {
        if (connectionEndpointIds.size > 0) {
            // send payload to host; the first and only endpoint in the list
            connectionsClient.sendPayload(
                connectionEndpointIds[0],
                buildEnqueuePayload(item)
            )
        }
    }

    /** Host only method */
    fun sendUpdatedQueue(queue: List<Item>) {
        val queuePayload = buildUpdatedQueuePayload(queue)
        connectionEndpointIds.forEach { guest ->
            connectionsClient.sendPayload(guest, queuePayload)
        }
    }

    /** Guest only method */
    fun sendSkipVote() {
        if (connectionEndpointIds.size > 0) {
            // send payload to host; the first and only endpoint in the list
            connectionsClient.sendPayload(
                connectionEndpointIds[0],
                buildSkipVotePayload()
            )
        }
    }

    fun disconnectFromParty() {
        _connected.value = Status.NO_ACTION
        connectionsClient.stopAdvertising()
        connectionsClient.stopAllEndpoints()
        connectionEndpointIds.clear()
    }


    /* Data reception methods and callbacks */

    /** Host only method */
    fun receiveQuery(requestingEndpointId: String, query: String?, isPaged: Boolean) {
        if (query == null) {
            // send back "error data" so guest isn't left hanging
            sendSearchResults(requestingEndpointId, null)
        } else {
            // perform a search for the guest and send back result
            GlobalScope.launch(Dispatchers.IO) {
                val res: SearchResult? = try {
                    if (isPaged) {
                        SpotifyRepository.getPagedSearchTrackResults(query)
                    } else {
                        SpotifyRepository.getSearchTrackResults(query)
                    }
                } catch (ex: Throwable) {
                    Timber.e("Error doing search for $requestingEndpointId:\n $ex")
                    // make sure guest gets a response back; null indicating error
                    null
                }
                sendSearchResults(requestingEndpointId, res)
            }
        }
    }

    /** Guest only method */
    fun receiveSearchResults(results: SearchResult?) {
        if (results != null) {
            SearchResultHandler.updateSearchResults(results)
            SearchResultHandler.setStatus(Status.SUCCESS)
        } else {
            Timber.e("Received null search result payload from host")
            // TODO: allow packing exceptions into SearchResult objects to enable better err msgs
            SearchResultHandler.setStatus(Status.ERROR)
        }
    }

    /** Host only method */
    fun receiveEnqueueRequest(item: Item?) = item?.run {
        QueueService.enqueueSong(item, true)
    }

    /** Guest only method */
    fun receiveUpdatedQueue(queue: List<Item>?) = queue?.run {
        QueueService.replaceQueue(queue)
    }

    /** Host only method */
    fun receiveSkipVoteRequest() {
        SkipSongHandler.voteSkip()
    }

    /**
     * This callback handles the reception of all incoming packets from connected devices.
     */
    private val payloadHandlerCallBacks = object : PayloadCallback() {
        /**
         * Called when a Payload is received from a remote endpoint. Depending on the type of
         * the Payload, all of the data may or may not have been received at the time of this call.
         * (Primarily, only large payload streams, like a file, would not arrive in 1 piece.)
         *
         * @param endpointId - the ID of the sender of the Payload being received
         * @param payload - the incoming data from `endpointId`. May be incomplete.
         */
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == BYTES) {
                // decompress payload and rebuild the ConnectionPayload obj
                val decompressedPayload = payload.asBytes()?.let { decompress(it) }
                Timber.d("Received payload $decompressedPayload")
                val parsedPayload = decompressedPayload?.let { json ->
                    PayloadBuilder.reconstructFromJson(
                        json,
                        ConnectionPayload::class.java
                    )
                }

                when (parsedPayload?.type) {
                    Type.QUERY -> {
                        val payloadKernel = PayloadBuilder.reconstructFromJson(parsedPayload.payload, String::class.java)
                        receiveQuery(endpointId, payloadKernel, isPaged = false)
                    }
                    Type.PAGED_SEARCH -> {
                        val payloadKernel = PayloadBuilder.reconstructFromJson(parsedPayload.payload, String::class.java)
                        receiveQuery(endpointId, payloadKernel, isPaged = true)
                    }
                    Type.UPDATE_QUEUE -> {
                        val payloadKernel = PayloadBuilder.reconstructFromJson(parsedPayload.payload, Array<Item>::class.java)
                        receiveUpdatedQueue(payloadKernel?.toList())
                    }
                    Type.ENQUEUE -> {
                        val payloadKernel = PayloadBuilder.reconstructFromJson(parsedPayload.payload, Item::class.java)
                        receiveEnqueueRequest(payloadKernel)
                    }
                    Type.SEARCH_RESULT -> {
                        val payloadKernel = PayloadBuilder.reconstructFromJson(parsedPayload.payload, SearchResult::class.java)
                        receiveSearchResults(payloadKernel)
                    }
                    Type.SKIP_VOTE -> receiveSkipVoteRequest()
                }
            } // else do nothing
        }

        /**
         * Called with progress info about an active Payload transfer, either incoming or outgoing.
         *
         * Since we only ever send BYTES payloads, they should all be small enough to arrive in 1
         * piece, so there is no need to implement any payload updates.
         */
        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }
}
