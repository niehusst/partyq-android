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

    fun buildQueryPayload(q: String): Payload = buildPayload(Type.QUERY, q)

    fun buildSearchResultPayload(res: SearchResult?): Payload = buildPayload(Type.SEARCH_RESULT, res)

    fun buildUpdatedQueuePayload(items: List<Item>): Payload = buildPayload(Type.UPDATE_QUEUE, items)

    fun buildEnqueuePayload(item: Item): Payload = buildPayload(Type.ENQUEUE, item)

    // no data is required to be sent; the type in itself is sufficient info
    fun buildSkipVotePayload(): Payload = buildPayload(Type.SKIP_VOTE, null)

    private fun buildPayload(type: Type, payload: Any?): Payload {
        val payloadKernel = gson.toJson(payload)
        val jsonPayload = gson.toJson(ConnectionPayload(type, payloadKernel))
        val compressedPayload = compress(jsonPayload)
        return Payload.fromBytes(compressedPayload)
    }
}
