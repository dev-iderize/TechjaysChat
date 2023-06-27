package com.techjays.chatlibrary.util

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
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
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.techjays.chatlibrary.ChatLibrary
import com.techjays.chatlibrary.R
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList

object Utility {

    private val df = DecimalFormat(".##########")

    fun formatDecimal(value: Double): String {
        return df.format(value)
    }


    fun findSuitableDrawables(fileType: String): Int {

        return when (fileType) {
            "image" -> R.drawable.lib_photo_24
            "video" -> R.drawable.lib_video_file_24
            "audio" -> R.drawable.lib_mic_24
            else -> R.drawable.lib_notifications_active_24
        }

    }


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

    fun replaceContactName(message: String, phoneNumber: String?, context: Context): String {

        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val uri: Uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber)
            )
            val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
            var contactName = ""
            val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    contactName = cursor.getString(0)
                }
                cursor.close()
            }
            if (contactName != "") {
                var replacedMessage = ""
                val matcher = getMatchedStars(message)
                while (matcher!!.find()) {
                    replacedMessage =
                        matcher!!.replaceAll(if (message.contains(":")) "$contactName:" else contactName)
                }
                if (replacedMessage != "") return replacedMessage
            }
        }

        return getBolded(message, getMatchedStars(message)!!, context).toString()
    }

    private fun getMatchedStars(text: String): Matcher? {
        return Pattern.compile("\\*(.*?)\\*").matcher(text)
    }

    private fun getBolded(text: String, matcher: Matcher, context: Context): Spannable {
        val noStarText = text.replace("*", "")

        val spannable: Spannable = SpannableString(noStarText)
        var count = 1
        while (matcher.find()) {
            spannable.setSpan(
                StyleSpan(Typeface.BOLD), matcher.start(),
                matcher.end() - count - 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                ForegroundColorSpan(getColor(context, R.color.dark_chocolate)),
                matcher.start(),
                matcher.end() - count - 1,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )

            count += 2
        }
        return spannable
    }

    fun getLibContactName(phoneNumber: String?, context: Context): String {
        if (phoneNumber.isNullOrEmpty())
            return ""
        else {
            if (phoneNumber != ChatLibrary.instance.mPhoneNumber) {
                if (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.READ_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val uri: Uri = Uri.withAppendedPath(
                        ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber)
                    )
                    val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    var contactName = ""
                    val cursor: Cursor? =
                        context.contentResolver.query(uri, projection, null, null, null)
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            contactName = cursor.getString(0)
                        }
                        cursor.close()
                    }
                    return contactName
                } else return ""
            } else
                return "You"
        }
    }

    fun notificationColor(type: String): Int {
        val color = when (type) {
            "SHIELD_ON" -> R.color.app_amber
            "SHIELD_OFF" -> R.color.dark_chocolate
            "SOS_ON" -> R.color.pastel_red
            "SOS_OFF" -> R.color.dark_chocolate
            else -> R.color.primary_color_light
        }
        return color
    }


    fun setChatNotification(
        first: String,
        third: String,
        color: Int,
        context: Context,
        second: String = " at "
    ): Spannable {
        val finalString = first + second + third
        val sb: Spannable = SpannableString(finalString)

        val bold = ResourcesCompat.getFont(context, R.font.public_sans_extra_bold) as Typeface
        sb.setSpan(
            bold,
            0,
            first.length + second.length + third.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        sb.setSpan(
            ForegroundColorSpan(getColor(context, color)), 0, first.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
        sb.setSpan(
            ForegroundColorSpan(getColor(context, R.color.hint_grey)),
            first.length,
            first.length + second.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
        sb.setSpan(
            ForegroundColorSpan(getColor(context, R.color.dark_chocolate)),
            second.length + first.length,
            first.length + second.length + third.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )

        return sb
    }

    fun displayLocalTime(time: String?): String {
        return if (time != null) {
            val inputFormat = SimpleDateFormat(
                if (time.endsWith("Z")) "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'" else "yyyy-MM-dd HH:mm:ss.SSSSSSXXX",
                Locale.getDefault()
            )
            val outputFormat = SimpleDateFormat("dd MMM yy hh:mm a", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val inputDate = inputFormat.parse(time)

            outputFormat.format(inputDate!!)
        } else {
            "unknown time"
        }

    }

    fun checkInternet(context: Context): Boolean {
        return if (Utility.isInternetAvailable(context))
            true
        else {
            AppDialogs.customOkAction(
                context,
                "No Internet"
            )

            false
        }
    }

    fun getLibraryRealPathFromUri(uri: Uri, context: Context): String? {
        var filePath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val columnIndex: Int = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = it.getString(columnIndex)
            }
            cursor.close()
        }
        return filePath
    }


    fun getRealPathFromURI(contentURI: Uri, context: Context): String? {
        val result: String
        val cursor: Cursor? = context.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) {
            result = contentURI.path!!
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    /*  fun uriTolibraryFile(context: Context, uri: Uri): File? {
          val filePath = getLibraryRealPathFromUri(context, uri)
          filePath?.let {
              return File(it)
          }
          return null
      }*/

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
        if (url == null) {
            return ""
        }

        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: ""
        }

        return ""
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
        val placeHolder: Int = R.drawable.ic_profile_icon
        loadUserImage(aURL, image, placeHolder)
    }

    fun loadPlaceholder(placeholder: Int, image: ImageView) {
        val placeholde: Int = R.drawable.ic_profile_icon
        Picasso.get().load(placeholder).placeholder(placeholde).fit().centerCrop()
            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(image)

    }

    fun loadUserImageWithCache(aURL: String?, image: ImageView, context: Context) {
        val placeHolder: Int = R.drawable.ic_profile_icon
        loadUserImageWithCache(aURL, image, placeHolder, context)
    }

    fun loadUserImageWithCache2(aURL: String?, image: ImageView, context: Context) {
        val placeHolder: Int = R.drawable.ic_profile_icon
        loadUserImageWithCache2(aURL, image, placeHolder, context)
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

    fun loadUserImageWithCache(
        aURL: String?,
        image: ImageView,
        placeHolder: Int,
        context: Context
    ) {
        try {
            if (aURL.isNullOrEmpty()) {
                image.setImageResource(placeHolder)
            } else {
                if (aURL.contains("http")) {
                    Glide
                        .with(context)
                        .load(aURL)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(image)
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


    fun loadUserImageWithCache2(
        aURL: String?,
        image: ImageView,
        placeHolder: Int,
        context: Context
    ) {
        try {
            if (aURL.isNullOrEmpty()) {
                image.setImageResource(placeHolder)
            } else {
                if (aURL.contains("http")) {
                    Glide
                        .with(context)
                        .load(aURL)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override(500, 500)
                        .into(image)
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