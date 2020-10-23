package com.niehusst.partyq.utility

import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.Inflater

object CompressionUtility {

    private const val BUFFER_SIZE = 1024

    fun compress(inputString: String): ByteArray {
        return try {
            // Encode a String into bytes
            val input = inputString.toByteArray(Charsets.UTF_8)
            val output = ByteArrayOutputStream(input.size)

            // set byte data into Deflater
            val compressor = Deflater()
            val buffer = ByteArray(BUFFER_SIZE)
            compressor.setInput(input)
            compressor.finish()

            while (!compressor.finished()) {
                // compress as much of input as possible into buffer
                val bytesCompressed = compressor.deflate(buffer)
                // write to out stream the amount of bytes actually compressed
                output.write(buffer, 0, bytesCompressed)
            }
            compressor.end()
            output.close()

            output.toByteArray()
        } catch (ex: Exception) {
            Timber.e("Compression error: $ex")
            // something went wrong trying to compress, so we'll just send it uncompressed
            inputString.toByteArray(Charsets.UTF_8)
        }
    }

    fun decompress(compressedBytes: ByteArray): String {
        return try {
            val decompressor = Inflater()
            decompressor.setInput(compressedBytes)
            val buffer = ByteArray(BUFFER_SIZE)
            val output = ByteArrayOutputStream(compressedBytes.size)

            while (!decompressor.finished()) {
                // decompress into buffer
                val decompressedBytes = decompressor.inflate(buffer)
                // write decompressed bytes to stream
                output.write(buffer, 0, decompressedBytes)
            }
            decompressor.end()
            output.close()

            // Decode the bytes into a String
            val decompressedBytes = output.toByteArray()
            String(decompressedBytes, 0, decompressedBytes.size, Charsets.UTF_8)
        } catch (ex: Exception) {
            Timber.e("Decompression error: $ex")
            // something went wrong decompressing, so just create String directly from bytes
            String(compressedBytes, Charsets.UTF_8)
        }
    }
}
