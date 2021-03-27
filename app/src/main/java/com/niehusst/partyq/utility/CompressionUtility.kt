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
            Timber.e(ex)
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
            Timber.e(ex)
            // something went wrong decompressing, so just create String directly from bytes
            String(compressedBytes, Charsets.UTF_8)
        }
    }
}
