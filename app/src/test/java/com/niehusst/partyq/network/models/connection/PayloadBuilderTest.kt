package com.niehusst.partyq.network.models.connection

import org.junit.Assert.*
import org.junit.Test

class PayloadBuilderTest {

    @Test
    fun `reconstruct good payload creates identical object`() {
        val payload = ConnectionPayload(Type.QUERY, "happy path")
        val jsonPayload = "{\"type\":\"QUERY\",\"payload\":\"happy path\"}"

        val reconstruction = PayloadBuilder.reconstructFromJson(jsonPayload, ConnectionPayload::class.java)

        assertEquals(payload, reconstruction)
    }

    @Test
    fun `attempt reconstructing bad payload is null`() {
        val badJson = "a;lskjeiafowie"
        val result = PayloadBuilder.reconstructFromJson(badJson, ConnectionPayload::class.java)

        assertNull(result)
    }
}
