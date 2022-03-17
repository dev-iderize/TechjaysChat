package com.techjays.chatlibrary.util

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.SystemClock
import android.text.Selection
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.techjays.chatlibrary.R
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.text.DecimalFormat

object Utility {

    private val df = DecimalFormat(".##########")

    fun formatDecimal(value: Double): String {
        return df.format(value)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun setBackgroundDrawableResource(window: Window, @DrawableRes drawableRes: Int) {
        val context = window.context
        val backgroundDrawable = ContextCompat.getDrawable(context, drawableRes) ?: return
        val transparentColor = ContextCompat.getColor(context, android.R.color.transparent)
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = transparentColor
            setBackgroundDrawable(backgroundDrawable)
        }
    }

    @SuppressLint("MissingPermission")
    fun isInternetAvailable(context: Context?): Boolean {
        try {
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo: NetworkInfo? = cm.activeNetworkInfo
            if (netInfo != null && netInfo.isConnected) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return false
    }

    /**
     * Get color value according to the version compat
     *
     * @param c     Context
     * @param color color resource int sample R.color.accent
     * @return Parsed int value equal to [android.graphics.Color] values
     */
    fun getColor(c: Context, color: Int): Int {
        return ContextCompat.getColor(c, color)
    }

    /**
     * Get color value according to the version compat
     *
     * @param c  Context
     * @param id drawable resource int sample R.drawable.rounded_bg
     * @return Parsed int value equal to [android.graphics.Color] values
     */
    fun getDrawable(c: Context, id: Int): Drawable {
        return ContextCompat.getDrawable(c, id)!!
    }

    fun errorText(text: String): String? {
        return String.format("%s should not be empty", text)

    }

    fun isUsingNightModeResources(context: Context): Boolean {
        return when (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }

    fun statusBarColor(window: Window, context: Context, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = context.resources.getColor(color)
        }
    }

    fun log(msg: String) {
        Log.d("FFFFFFFF ---> ", msg)
    }

    fun get_roundImage(c: Context, bitmap: Bitmap): Bitmap? {
        try {
            val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val rect = Rect(0, 0, bitmap.width, bitmap.height)

            val paint = Paint()
            paint.isAntiAlias = true

            val canvas = Canvas(output)
            canvas.drawARGB(0, 0, 0, 0)
            canvas.drawCircle(
                (bitmap.width / 2).toFloat(),
                (bitmap.height / 2).toFloat(),
                (bitmap.width / 2).toFloat(),
                paint
            )
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

            canvas.drawBitmap(bitmap, rect, rect, paint)

            return output

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun encodeFileToBase64Binary(filePath: String): String? {
        var encodedfile: String? = null
        try {
            val file = File(filePath)
            val fileInputStreamReader = FileInputStream(file)
            val bytes = ByteArray(file.length().toInt())
            fileInputStreamReader.read(bytes)
            encodedfile = Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return encodedfile
    }

    fun decodeFileToBase64Binary(data: String): Bitmap? {
        val decodedString = Base64.decode(data, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        return decodedByte
    }

    fun colorString(text: String): String? {
        return String.format("<b>%s</b>", text)
    }

    fun noColorString(text: String): String? {
        return String.format("%s", text)
    }

    fun getFirstLetterCaps(text: String): String? {
        try {
            val output = String.format("%s", text.substring(0, 1).toUpperCase() + text.substring(1))
            return output
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    fun convert(url: String): ArrayList<String> {
        val urls = ArrayList<String>()
        urls.addAll(url.split(","))
        return urls
    }

    fun dptopixel(context: Context, pixel: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            pixel.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    /**
     * Add a custom text as a static prefix for Edittext
     */
    @SuppressLint("SetTextI18n")
    fun EditText.stickPrefix(prefix: String) {
        this.setOnClickListener {
            this.setText(prefix)
            Selection.setSelection(this.text, this.text!!.length)
        }

        this.addTextChangedListener(onTextChanged = { _: CharSequence?, _: Int, _: Int, _: Int ->
            if (this.text!!.isEmpty()) {
                this.setText(prefix)
                Selection.setSelection(
                    this.text,
                    this.text!!.length
                )
            }
        })

        this.addTextChangedListener(afterTextChanged = {
            if (!it.toString().startsWith(prefix) && it?.isNotEmpty() == true) {
                this.setText(prefix)
                Selection.setSelection(this.text, this.text!!.length)
            }
        })
    }


    fun getETValue(aEditText: EditText?): String {
        return aEditText?.text?.toString()?.trim { it <= ' ' } ?: ""
    }

    fun getTXTValue(aTextText: TextView?): String {
        return aTextText?.text?.toString()?.trim { it <= ' ' } ?: ""
    }


    /**
     * Get Mime Type
     * url = file path or whatever suitable URL you want.
     * return - String
     */

    fun getMimeType(url: String?): String {
        var type = ""
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)!!
        }
        return type
    }

    var mLastClickTime = 0L
    fun isOpenRecently(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return true
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return false
    }

    fun loadUserImage(aURL: String?, image: ImageView, context: Context) {
        val placeHolder: Int = R.drawable.lib_ic_user_placeholder
        loadUserImage(aURL, image, placeHolder)
    }

    fun loadPlaceholder(placeholder: Int, image: ImageView) {
        val placeholde: Int = R.drawable.lib_ic_user_placeholder
        Picasso.get().load(placeholder).placeholder(placeholde).fit().centerCrop()
            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(image)

    }

    fun loadUserImageWithCache(aURL: String?, image: ImageView, context: Context) {
        val placeHolder: Int = R.drawable.lib_ic_user_placeholder
        loadUserImageWithCache(aURL, image, placeHolder,context)
    }


    /**
     * Load images
     */
    fun loadUserImage(aURL: String?, image: ImageView, placeHolder: Int) {
        try {
            if (aURL.isNullOrEmpty()) {
                image.setImageResource(placeHolder)
            } else {
                if (aURL.contains("http")) {
                    Picasso.get().load(aURL)
                        .placeholder(placeHolder)
                        .error(placeHolder)
                        .fit().centerCrop()
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(image)
                } else {
                    Picasso.get().load(File(aURL))
                        .placeholder(placeHolder)
                        .error(placeHolder)
                        .fit().centerCrop()
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(image)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadUserImageWithCache(aURL: String?, image: ImageView, placeHolder: Int,context: Context) {
        try {
            if (aURL.isNullOrEmpty()) {
                image.setImageResource(placeHolder)
            } else {
                if (aURL.contains("http")) {
                    Glide
                        .with(context)
                        .load(aURL )
                        .diskCacheStrategy( DiskCacheStrategy.ALL )
                        .fitCenter()
                        .into(image);
                } else {
                    Picasso.get().load(File(aURL))
                        .placeholder(placeHolder)
                        .error(placeHolder)
                        .fit().centerCrop()
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(image)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}