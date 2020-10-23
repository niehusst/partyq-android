package com.niehusst.partyq.utility

import com.google.android.gms.nearby.connection.Payload
import com.google.gson.GsonBuilder
import com.niehusst.partyq.network.models.api.Item
import com.niehusst.partyq.network.models.api.SearchResult
import com.niehusst.partyq.network.models.connection.ConnectionPayload
import com.niehusst.partyq.network.models.connection.Type
import com.niehusst.partyq.utility.CompressionUtility.compress
import timber.log.Timber

object PayloadBuilder {

    private val gson = GsonBuilder().create()

    fun <T> reconstructFromJson(jsonString: String, classOfT: Class<T>): T? {
        return try {
            gson.fromJson(jsonString, classOfT)
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }
    }

    fun buildQueryPayload(q: String): Payload {
        return buildPayload(
            Type.QUERY,
            q
        )
    }

    fun buildSearchResultPayload(res: SearchResult?): Payload {
        return buildPayload(
            Type.SEARCH_RESULT,
            res
        )
    }

    fun buildUpdatedQueuePayload(items: List<Item>): Payload {
        return buildPayload(
            Type.UPDATE_QUEUE,
            items
        )
    }

    fun buildEnqueuePayload(item: Item): Payload {
        return buildPayload(
            Type.ENQUEUE,
            item
        )
    }

    fun buildSkipVotePayload(): Payload {
        // no data is required to be sent; the type in itself is sufficient info
        return buildPayload(
            Type.SKIP_VOTE,
            null
        )
    }

    private fun buildPayload(type: Type, payload: Any?): Payload {
        val payloadKernel = gson.toJson(payload)
        val jsonPayload = gson.toJson(
            ConnectionPayload(
                type,
                payloadKernel
            )
        )
        val compressedPayload = compress(jsonPayload)
        return Payload.fromBytes(compressedPayload)
    }
}
