package com.techjays.chatlibrary.helpers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object ImageUtils {
    private var totalImagesRequested = 0
    private var totalAspectRatiosReceived = 0
    fun getAspectRatioFromUrl(imageUrl: String, callback: AspectRatioCallback) {
        totalImagesRequested++
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(imageUrl)
                val connection = withContext(Dispatchers.IO) {
                    url.openConnection()
                } as HttpURLConnection
                connection.doInput = true
                withContext(Dispatchers.IO) {
                    connection.connect()
                }

                val inputStream: InputStream = connection.inputStream
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                val width = bitmap.width
                val height = bitmap.height

                val aspectRatio: Float = width.toFloat() / height.toFloat()

                bitmap.recycle()

                withContext(Dispatchers.Main) {
                    callback.onAspectRatioLoaded(aspectRatio, imageUrl)
                    totalAspectRatiosReceived++

                    if (totalAspectRatiosReceived == totalImagesRequested) {
                        callback.onEveryImageLoaded()
                    }
                }
            } catch (e: IOException) {
                Log.e("com.techjays.chatlibrary.helpers.ImageUtils", "Error loading image from URL: ${e.message}")
                withContext(Dispatchers.Main) {
                    callback.onAspectRatioLoaded(
                        0f,
                        imageUrl
                    )
                }
            }
        }
    }
}
interface AspectRatioCallback {
    fun onAspectRatioLoaded(aspectRatio: Float, imageUrl: String)
    fun onEveryImageLoaded()
}
