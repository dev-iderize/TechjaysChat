package com.techjays.chatlibrary.base

import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.Util.AppDialogs
import com.techjays.chatlibrary.Util.Utility


abstract class BaseActivity : AppCompatActivity() {

    abstract fun clickListener()

    abstract fun init()

    fun checkInternet(): Boolean {
        return if (Utility.isInternetAvailable(this))
            true
        else {
            AppDialogs.customOkAction(
                this,
                "No Internet")

            false
        }
    }

    fun getETValue(aEditText: EditText?): String {
        return aEditText?.text?.toString()?.trim { it <= ' ' } ?: ""
    }

    fun getTXTValue(aTextText: TextView?): String {
        return aTextText?.text?.toString()?.trim { it <= ' ' } ?: ""
    }

}