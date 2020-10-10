package com.niehusst.partyq.network.models.connection

import com.google.gson.GsonBuilder
import org.junit.Assert.*
import org.junit.Test

class PayloadBuilderTest {

    @Test
    fun `build and reconstruct good payload`() {
        val gson = GsonBuilder().create()

        val payload = ConnectionPayload(Type.QUERY, "happy path")
        val jsonPayload = gson.toJson(payload)

        val reconstruction = PayloadBuilder.reconstructPayloadFromJson(jsonPayload)

        assertEquals(payload, reconstruction)
    }

    @Test
    fun `attempt reconstructing bad payload is null`() {
        val badJson = "a;lskjeiafowie"
        val result = PayloadBuilder.reconstructPayloadFromJson(badJson)

        assertNull(result)
    }
}
