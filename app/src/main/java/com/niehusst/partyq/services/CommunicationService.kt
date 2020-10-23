package com.niehusst.partyq.services

import android.Manifest
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.nearby.connection.Payload.Type.BYTES
import com.niehusst.partyq.network.Status
import com.niehusst.partyq.network.models.api.Item
import com.niehusst.partyq.network.models.api.SearchResult
import com.niehusst.partyq.network.models.connection.ConnectionPayload
import com.niehusst.partyq.network.models.connection.PayloadBuilder
import com.niehusst.partyq.network.models.connection.PayloadBuilder.buildEnqueuePayload
import com.niehusst.partyq.network.models.connection.PayloadBuilder.buildQueryPayload
import com.niehusst.partyq.network.models.connection.PayloadBuilder.buildSearchResultPayload
import com.niehusst.partyq.network.models.connection.PayloadBuilder.buildSkipVotePayload
import com.niehusst.partyq.network.models.connection.PayloadBuilder.buildUpdatedQueuePayload
import com.niehusst.partyq.network.models.connection.Type
import com.niehusst.partyq.repository.SpotifyRepository
import com.niehusst.partyq.utility.CompressionUtility.decompress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

object CommunicationService { // TODO: think about making this into a bound service

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
        Manifest.permission.ACCESS_FINE_LOCATION // TODO: make sure that location is enabled (manually) on the device??? or is confirming modal good enough?
    )

    // list of IDs of devices connected to the device
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
                        sendUpdatedQueue(QueueService.getQueueItems()) // TODO: this isnt working

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
        // TODO: add on failure listener to stop app if we cant connect people to party?? or will it crash itself already
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
                }
            }

            override fun onDisconnected(endpointId: String) {
                // TODO: nav away to party end activity. do i have to have some global Livedata to monitor so PartyActivity knows to do something???
            }
        }


    /* Data sending methods */

    /** Guest only method */
    fun sendQuery(query: String) {
        if (connectionEndpointIds.size > 0) {
            // send payload to host; the first and only endpoint in the list
            connectionsClient.sendPayload(
                connectionEndpointIds[0],
                buildQueryPayload(query)
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
    fun sendUpdatedQueue(queue: List<Item>) { // TODO: this should be done periodically. with a job?
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
        // TODO: process is diff for guests ??
    }


    /* Data reception methods and callbacks */

    fun receiveQuery(requestingEndpointId: String, query: String?) = query?.run {
        // TODO: return an error to sender if query is null
        // perform a search for the guest and send back result
        GlobalScope.launch(Dispatchers.IO) {
            val res = SpotifyRepository.getSearchTrackResults(query)
            sendSearchResults(requestingEndpointId, res)
        }
    }

    fun receiveSearchResults(results: SearchResult?) {
        if (results != null) {
            SearchResultHandler.updateSearchResults(results)
            SearchResultHandler.setStatus(Status.SUCCESS)
        } else {
            Timber.e("Received null search result payload from host")
            SearchResultHandler.setStatus(Status.ERROR)
        }
    }

    fun receiveEnqueueRequest(item: Item?) = item?.run {
        QueueService.enqueueSong(item, true) // TODO: this should only ever be run by host.. is this ok?
    }

    fun receiveUpdatedQueue(queue: List<Item>?) = queue?.run {
        QueueService.replaceQueue(queue)
    }

    fun receiveSkipVote() {
        // TODO: call skip vote
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
                        receiveQuery(endpointId, payloadKernel)
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
                    Type.SKIP_VOTE -> receiveSkipVote()
                }
            } // else do nothing
        }

        /**
         * Called with progress info about an active Payload transfer, either incoming or outgoing.
         */
        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            if (update.status == PayloadTransferUpdate.Status.SUCCESS) {
                // TODO: do i need this? hopefully everything should arrive in 1 package
                Timber.e("Received a payload update for some reason; a payload was too large.")
            }
        }
    }
}
