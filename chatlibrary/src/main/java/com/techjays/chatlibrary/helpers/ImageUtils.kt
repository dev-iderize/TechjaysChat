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
    fun getAspectRatioFromUrl(imageUrl: String, callback: AspectRatioCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                val inputStream: InputStream = connection.inputStream
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                val width = bitmap.width
                val height = bitmap.height

                val aspectRatio: Float = width.toFloat() / height.toFloat()

                bitmap.recycle()

                withContext(Dispatchers.Main) {
                    callback.onAspectRatioLoaded(aspectRatio, imageUrl)
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
}
