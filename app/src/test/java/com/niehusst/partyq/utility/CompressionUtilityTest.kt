package com.niehusst.partyq.utility

import com.google.android.gms.nearby.connection.ConnectionsClient.MAX_BYTES_DATA_SIZE
import com.google.gson.GsonBuilder
import com.niehusst.partyq.network.models.api.*
import com.niehusst.partyq.network.models.connection.ConnectionPayload
import com.niehusst.partyq.network.models.connection.Type
import org.junit.Assert.*
import org.junit.Test
import kotlin.text.Charsets.UTF_8

class CompressionUtilityTest {

    @Test
    fun `simple string compression and decompression`() {
        val toCompress = "having a good time with Partyq!"
        val compressedBytes = CompressionUtility.compress(toCompress)
        val decompressedString = CompressionUtility.decompress(compressedBytes)
        assertEquals(toCompress, decompressedString)
    }

    @Test
    fun `json string compression and decompression is lossless`() {
        val jsonToCompress = buildBigString(1)
        val compressedBytes = CompressionUtility.compress(jsonToCompress)
        val decompressedString = CompressionUtility.decompress(compressedBytes)
        assertEquals(jsonToCompress, decompressedString)
    }

    @Test
    fun `bad input compression`() {

    }

    @Test
    fun `bad input decompression`() {

    }

    @Test
    fun `confirmation of payload size reduction from compression for large payload`() {
        val preCompression = buildBigString(10)
        val uncompressedBytes = preCompression.toByteArray(UTF_8)
        val compressedBytes = CompressionUtility.compress(preCompression)
        // assert compressed payload is smaller than uncompressed version
        println("comp size ${compressedBytes.size} <? uncomp size ${uncompressedBytes.size}")
        assert(compressedBytes.size < uncompressedBytes.size)

        // assert compressed payload is smaller than Nearby Connections max payload size
        assert(compressedBytes.size < MAX_BYTES_DATA_SIZE)
    }

    private fun buildBigString(numObjs: Int): String {
        // create some dummy objects
        val objList = mutableListOf<Item>()
        for (i in 0..numObjs) {
            objList.add(Item(
                album = Album(
                    albumType = "album",
                    artists = listOf(
                        Artist(
                            externalUrls = ExternalUrls("none here"),
                            name = "Hanah Montana",
                            href = "https://api.spotify.com/artist/agatha_christie",
                            id = "alejtaoi8943",
                            type = "artist",
                            uri = "spotify:totallyMusicArtist"
                        ),
                        Artist(
                            externalUrls = ExternalUrls("none here"),
                            name = "Jack Black",
                            href = "https://api.spotify.com/artist/agatha_christie",
                            id = "alejtaoi8943",
                            type = "artist",
                            uri = "spotify:totallyMusicArtist"
                        )
                    ),
                    availableMarkets = listOf("US", "UK", "EU", "JP", "SA"),
                    externalUrls = ExternalUrls("https://spotify.spotify.spotify.com"),
                    href = "https://www.hackerrank.com/challenges/",
                    id = "wlij238fskaE8fos",
                    images = listOf(
                        Image(300, "https://yummyimage.png", 300),
                        Image(500, "https://yummyerimage.png", 500)
                    ),
                    name = "My almbug",
                    releaseDate = "10/10/2010",
                    releaseDatePrecision = "yes??",
                    totalTracks = 10,
                    type = "album",
                    uri = "spotify:tortelinie"
                ),
                artists = listOf(
                    Artist(
                        externalUrls = ExternalUrls("none here"),
                        name = "Agatha Christie",
                        href = "https://api.spotify.com/artist/agatha_christie",
                        id = "alejtaoi8943",
                        type = "artist",
                        uri = "spotify:totallyMusicArtist"
                    )
                ),
                availableMarkets = listOf("US", "UK", "EU", "JP", "SA"),
                discNumber = 1,
                durationMs = 1349269,
                explicit = false,
                externalIds = ExternalIds("someIDthingy"),
                externalUrls = ExternalUrls("https://spotify.spotify.spotify.com"),
                href = "https://www.hackerrank.com/challenges/",
                id = "wlij238fskaE8fos",
                isLocal = true,
                name = "Scrimmy Bingus and the Crungy Spingus",
                popularity = 1000,
                previewUrl = "https://developers.google.com/nearby/connections/android/exchange-data",
                trackNumber = 2,
                type = "track",
                uri = "spotify:jjdJS40sjwoie38a3U"
            ))
        }

        // build json payload object, like the app does
        val gson = GsonBuilder().create()
        val payload = ConnectionPayload(Type.SEARCH_RESULT, objList)
        return gson.toJson(payload)
    }
}
