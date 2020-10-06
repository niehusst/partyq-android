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
    const val SERVICE_ID = "com.niehusst.partyq" // just something unique to the app
    const val REQUEST_CODE_REQUIRED_PERMISSIONS = 1
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val connectionEndpointIds = listOf<String>()

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
                override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                    if (result.status.isSuccess) {
                        Timber.i("Nearby API successfully connected to $endpointId")
                        // TODO: do we have to also be trying to discover in order to send data back?
                    } else {
                        Timber.e("Nearby API advertising; an endpoint connection failed")
                    }
                }

                override fun onDisconnected(p0: String) {
                    TODO("Not yet implemented")
                }

                override fun onConnectionInitiated(p0: String, p1: ConnectionInfo) {
                    TODO("Not yet implemented")
                }

            },
            advertOptions
        )
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
}
