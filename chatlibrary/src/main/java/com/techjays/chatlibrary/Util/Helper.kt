package com.techjays.chatlibrary.Util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.FragmentActivity
import com.techjays.chatlibrary.base.LibFragmentManager
import java.util.*

object Helper {

    fun getTimeZone(): ArrayList<String>? {
        val timezones = TimeZone.getAvailableIDs()
        val list = ArrayList<String>()

        for (i in timezones.indices) {
            val zone = TimeZone.getTimeZone(timezones[i]).getDisplayName(true, TimeZone.SHORT)
            val name = timezones[i]

            val out = String.format("(%s) %s", zone, name)
            list.add(out)
        }
        return list
    }

    fun isValidEmail(target: String): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun isValidMobileNo(target: String): Boolean {
        return !TextUtils.isEmpty(target) && target.length == 10
    }

    /**
     * @param context Context
     */
    fun navigateAppSetting(context: Context) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }

    /**
     * @param context Context
     */
    fun navigatePlaystore(context: Context) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$context.packageName")
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * @param context Context
     */
    fun openBrowser(context: Context, url: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * @param context Context
     */
    fun navigateGoogleMap(context: Context, lat: String, long: String) {
        try {
            if (lat.isEmpty() || long.isEmpty())
                return
            val navigation = Uri.parse(String.format("google.navigation:q=%s,%s", lat, long))
            val navigationIntent = Intent(Intent.ACTION_VIEW, navigation)
            navigationIntent.setPackage("com.google.android.apps.maps")
            context.startActivity(navigationIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * @param context Context
     */
    fun navigateURL(context: Context, mURL: String) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(mURL)
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }




    fun navigateProfile(activity: FragmentActivity, id: String, showHeader: Boolean) {
        if (Utility.isInternetAvailable(activity)) {
            val mFragmentManager = LibFragmentManager(activity)
            val bundle = Bundle()
            bundle.putString("user_id", id)
            bundle.putBoolean("showHeader", showHeader)
        }
    }




    fun navigateProfile(activity: FragmentActivity, id: String) {
        navigateProfile(activity, id, true)
    }


    fun getRegion(context: Context): String {
        val region = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return region.networkCountryIso
    }

    /**
     * It's work for all the source - Google photos, Gallery and Camera
     */
    fun getRealPathFromURI(context: Context, uri: Uri): String {
        var result: String? = null
        try {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            if (cursor == null)
                result = uri.path
            else {
                cursor.moveToFirst()
                val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                result = cursor.getString(idx)
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result!!
    }



    /**
     * @param activity Activity
     */
    fun logout(activity: Activity) {
        activity.finish()
    }
}