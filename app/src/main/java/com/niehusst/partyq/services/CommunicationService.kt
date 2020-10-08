package com.niehusst.partyq.services

import android.Manifest
import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.nearby.connection.Payload.Type.BYTES
import com.google.gson.GsonBuilder
import com.niehusst.partyq.network.models.api.Item
import com.niehusst.partyq.network.models.connection.PayloadBuilder.buildEnqueuePayload
import com.niehusst.partyq.network.models.connection.PayloadBuilder.buildQueryPayload
import com.niehusst.partyq.network.models.connection.PayloadBuilder.buildSearchResultPayload
import com.niehusst.partyq.network.models.connection.PayloadBuilder.buildSkipVotePayload
import com.niehusst.partyq.network.models.connection.PayloadBuilder.buildUpdatedQueuePayload
import com.niehusst.partyq.network.models.connection.PayloadBuilder.reconstructPayloadFromJson
import com.niehusst.partyq.network.models.connection.Type
import com.niehusst.partyq.utility.CompressionUtility.decompress
import timber.log.Timber

object CommunicationService { // TODO: think about making this into a bound service

    private lateinit var connectionsClient: ConnectionsClient

    private val gson = GsonBuilder().create()

    private val STRATEGY = Strategy.P2P_STAR
    private const val SERVICE_ID = "com.niehusst.partyq" // just something unique to the app
    const val REQUEST_CODE_REQUIRED_PERMISSIONS = 1
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // list of IDs of devices connected to the device
    val connectionEndpointIds = mutableListOf<String>()

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
                        connectionsClient.acceptConnection(endpointId, payloadHandlerCallBacks)
                    } else {
                        connectionsClient.rejectConnection(endpointId)
                    }
                }

                override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                    if (result.status.isSuccess) {
                        Timber.i("Nearby API successfully connected to $endpointId")

                        connectionEndpointIds.add(endpointId)

                        // dont stop advertising; keep connecting to guests until party ends
                    } else {
                        Timber.e("Nearby API advertising; an endpoint connection failed")
                    }
                }

                override fun onDisconnected(endpointId: String) {
                    connectionEndpointIds.remove(endpointId)
                }
            },
            advertOptions
        ) // TODO: add on failure listener to stop app if we cant connect people to party?? or will it crash itself already
    }

    fun connectToParty(inputPartyCode: String) {
        // TODO: start/connect to party net
        val discoverOptions = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startDiscovery(
            inputPartyCode,
            object : EndpointDiscoveryCallback() {
                override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                    // double check verify that party codes match before attempting connection
                    if (info.endpointName == inputPartyCode) {
                        Timber.i("Attempting conncection to $endpointId")
                        connectionsClient.requestConnection(
                            inputPartyCode,
                            endpointId,
                            buildGuestConnectionLifecycleCallback(inputPartyCode)
                        )
                    }
                }

                override fun onEndpointLost(endpointId: String) { /* no-op */ }
            },
            discoverOptions
        )
    }

    private fun buildGuestConnectionLifecycleCallback(inputPartyCode: String) =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
                connectionsClient.acceptConnection(endpointId, payloadHandlerCallBacks)
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                if (result.status.isSuccess) {
                    Timber.i("Nearby API successfully connected to $endpointId")

                    connectionEndpointIds.add(endpointId)
                    connectionsClient.stopDiscovery() // only connect to 1 host
                } else {
                    Timber.e("Nearby API advertising; an endpoint connection failed")
                }
            }

            override fun onDisconnected(endpointId: String) {
                // TODO: nav away to party end activity
            }
        }


    /* Data sending methods */

    /** Guest only method */
    fun sendSearchRequest(query: String) {
        if (connectionEndpointIds.size > 0) {
            // send payload to host; the first and only endpoint in the list
            connectionsClient.sendPayload(
                connectionEndpointIds[0],
                buildQueryPayload(query)
            )
        }
    }

    /** Host only method */
    fun sendSearchResults(requestingEndpointId: String, results: List<Item>) {
        connectionsClient.sendPayload(
            requestingEndpointId,
            buildSearchResultPayload(results)
        )
    }

    /** Guest only method */
    fun sendEnqueueRequest(item: Item) { // TODO: cut down on unused fields in Item to make payload smaller?
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
        connectionEndpointIds.forEach { guest ->
            connectionsClient.sendPayload(guest, buildUpdatedQueuePayload(queue))
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
        // TODO: host disconnect from guests. also stop advertising
        connectionsClient.stopAdvertising()
        connectionsClient.stopAllEndpoints()
        // TODO: process is diff for guests ??
    }


    /* Data reception methods and callbacks */

    fun receiveSearchRequest() {

    }

    fun receiveSearchResults() {

    }

    fun receiveEnqueueRequest() {

    }

    fun receiveUpdatedQueue(queue: List<Item>) {
        QueueService.replaceQueue(queue)
    }

    fun receiveSkipVote() {

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
                val decompressedPayload = decompress(payload.asBytes()!!)
                val parsedPayload = reconstructPayloadFromJson(decompressedPayload)

                when (parsedPayload.type) {
                    Type.QUERY -> {
                        val query = parsedPayload.payload as? String
                        query?.run {
                            // TODO: perform a search here and another comms call to send back result
                        }
                    }
                    Type.UPDATE_QUEUE -> {
                        val newQueue = parsedPayload.payload as? List<Item>
                        newQueue?.run {
                            QueueService.replaceQueue(newQueue)
                        }
                    }
                    Type.ENQUEUE -> {
                        val item = parsedPayload.payload as? Item
                        item?.run {
                            QueueService.enqueueSong(item, true) // TODO: this should only ever be run by host.. is this ok?
                        }
                    }
                    Type.SEARCH_RESULT -> {
                        val searchResults = parsedPayload.payload as? List<Item>
                        // TODO: get the results back to UI somehow
                    }
                    Type.SKIP_VOTE -> {
                        // TODO: call skip vote
                    }
                }
            } // else do nothing
        }

        /**
         * Called with progress info about an active Payload transfer, either incoming or outgoing.
         */
        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            if (update.status == PayloadTransferUpdate.Status.SUCCESS) {
                // TODO: do i need this? hopefully everything should arrive in 1 package
            }
        }
    }
}
