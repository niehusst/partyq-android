package com.niehusst.partyq.utility

import java.util.zip.Deflater
import java.util.zip.Inflater

object CompressionUtility {

    // enough bytes to reliably hold our largest quantity of data once compressed
    // (usually our largest data packages aren't even 1000B)
    private const val BUFFER_SIZE = 5000

    fun compress(inputString: String): ByteArray {
        return try {
            // Encode a String into bytes
            val input = inputString.toByteArray(Charsets.UTF_8)

            // Compress the bytes
            val compressor = Deflater()
            val output = ByteArray(BUFFER_SIZE)
            compressor.setInput(input)
            compressor.finish()
            val compressedDataLength = compressor.deflate(output)
            compressor.end()

            output
        } catch (ex: Exception) {
            // something went wrong trying to compress, so we'll just send it uncompressed
            inputString.toByteArray(Charsets.UTF_8)
        }
    }

    fun decompress(compressedBytes: ByteArray): String {
        return try {
            // Decompress the bytes
            val decompressor = Inflater()
            decompressor.setInput(compressedBytes, 0, BUFFER_SIZE)
            val result = ByteArray(BUFFER_SIZE)
            val resultLength: Int = decompressor.inflate(result)
            decompressor.end()

            // Decode the bytes into a String
            String(result, 0, resultLength, Charsets.UTF_8)
        } catch (ex: Exception) {
            // something went wrong decompressing, so just create String directly from bytes
            String(compressedBytes, Charsets.UTF_8)
        }
    }
}
