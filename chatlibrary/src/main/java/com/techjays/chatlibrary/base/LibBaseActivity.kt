package com.techjays.chatlibrary.base

import android.text.Editable
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.techjays.chatlibrary.util.AppDialogs
import com.techjays.chatlibrary.util.Utility


abstract class LibBaseActivity : AppCompatActivity() {

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

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)


}