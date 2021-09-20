package com.techjays.chatlibrary.base

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.Util.AppDialogs
import com.techjays.chatlibrary.Util.Utility

abstract class BaseFragment : Fragment() {

    abstract fun onBackPressed()

    abstract fun onResumeFragment()

    abstract fun init(view: View)

    abstract fun initBundle()

    abstract fun clickListener()

    fun checkInternet(): Boolean {
        return if (Utility.isInternetAvailable(this.context))
            true
        else {
            AppDialogs.customOkAction(
                requireActivity(),
                "No Internet"
            )
            false
        }
    }

    override fun onResume() {
        onResumeFragment()
        super.onResume()
    }

    fun getETValue(aEditText: EditText?): String {
        return aEditText?.text?.toString()?.trim { it <= ' ' } ?: ""
    }

    fun getTXTValue(aTextText: TextView?): String {
        return aTextText?.text?.toString()?.trim { it <= ' ' } ?: ""
    }

}
