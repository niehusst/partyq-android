package com.niehusst.partyq.network.models.connection

import com.google.android.gms.nearby.connection.Payload
import com.google.gson.GsonBuilder
import com.niehusst.partyq.network.models.api.Item
import com.niehusst.partyq.utility.CompressionUtility.compress

object PayloadBuilder {

    private val gson = GsonBuilder().create()

    fun reconstructPayloadFromJson(jsonString: String): ConnectionPayload {
        return gson.fromJson(jsonString, ConnectionPayload::class.java)
    }

    fun buildQueryPayload(q: String): Payload {
        return buildPayload(Type.QUERY, q)
    }

    fun buildSearchResultPayload(items: List<Item>): Payload {
        return buildPayload(Type.SEARCH_RESULT, items)
    }

    fun buildUpdatedQueuePayload(items: List<Item>): Payload {
        return buildPayload(Type.UPDATE_QUEUE, items)
    }

    fun buildEnqueuePayload(item: Item): Payload {
        return buildPayload(Type.ENQUEUE, item)
    }

    fun buildSkipVotePayload(): Payload {
        return buildPayload(Type.SKIP_VOTE, null)
    }

    private fun buildPayload(type: Type, payload: Any?): Payload {
        val jsonPayload = gson.toJson(ConnectionPayload(type, payload))
        val compressedPayload = compress(jsonPayload)
        return Payload.fromBytes(compressedPayload)
    }
}
