package com.techjays.chatlibrary.helpers

import android.util.Log
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Okio
import okio.Sink

class ShieldUpChatLibProgressRequestBody(
    private val requestBody: RequestBody,
    private val progressCallback: (Int) -> Unit,
    private val errorCallback: (Throwable) -> Unit,
    private val completionCallback: () -> Unit
) : RequestBody() {

    override fun contentType(): MediaType? = requestBody.contentType()

    override fun contentLength(): Long = requestBody.contentLength()

    override fun writeTo(sink: BufferedSink) {
        val countingSink = CountingSink(sink)
        val bufferedSink = Okio.buffer(countingSink)

        try {
            requestBody.writeTo(bufferedSink)
            bufferedSink.flush()
            completionCallback()
        } catch (e: Exception) {
            errorCallback(e)
        }
    }

    private inner class CountingSink(delegate: Sink) : ForwardingSink(delegate) {
        private var uploadedBytes = 0L

        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            uploadedBytes += byteCount
            val progress = (uploadedBytes * 100 / contentLength()).toInt()
            progressCallback(progress)
        }
    }
}

