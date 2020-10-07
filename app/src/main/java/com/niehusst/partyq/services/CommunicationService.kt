package com.niehusst.partyq.services

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.niehusst.partyq.network.models.Item
import android.Manifest
import com.google.android.gms.nearby.connection.*
import timber.log.Timber

object CommunicationService { // TODO: think about making this into a bound service

    private lateinit var connectionsClient: ConnectionsClient

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

    val connectionEndpointIds = mutableListOf<String>()

    /**
     * This function must be called before any other in order to initialize `connectionsClient`
     */
    fun start(context: Context) {
        connectionsClient = Nearby.getConnectionsClient(context)
    }

    /**
     * Broadcast the calling device's availability for connection via Nearby Connections API.
     * The device should continue advertising until `disconnectFromParty()` is called.
     * Host only method.
     */
    fun hostAdvertise(partyCode: String) {
        val advertOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startAdvertising(
            partyCode, // human readable identifier for this party
            SERVICE_ID, // app identifier
            object : ConnectionLifecycleCallback() {
                override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                    // TODO: verify matching party code (from connectionInfo) here?
                    connectionsClient.acceptConnection(endpointId, payloadHandlerCallBacks)
                }

                override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                    if (result.status.isSuccess) {
                        Timber.i("Nearby API successfully connected to $endpointId")

                        connectionEndpointIds.add(endpointId)

                        // TODO: do we have to also be trying to discover in order to send data back?
                    } else {
                        Timber.e("Nearby API advertising; an endpoint connection failed")
                    }
                }

                override fun onDisconnected(p0: String) {
                    TODO("Not yet implemented")
                }
            },
            advertOptions
        ).addOnFailureListener {

        }
    }

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
        // TODO: send disconnect message to all connections, then disconnect from them. also stop advertising
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
            // TODO: payload should include some identifier for what op it is
            when(payload) {

            }
        }

        /**
         * Called with progress info about an active Payload transfer, either incoming or outgoing.
         */
        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            TODO("Not yet implemented")
        }
    }
}
