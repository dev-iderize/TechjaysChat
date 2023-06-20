package com.techjays.chatlibrary.util

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class LibBindingAdapters {
    companion object {

        @JvmStatic
        @BindingAdapter("display_local_time")
        fun displayLocalTime(view: TextView, time: String?) {
            if (time != null) {
                val inputFormat = SimpleDateFormat(
                    if (time.endsWith("Z")) "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'" else "yyyy-MM-dd HH:mm:ss.SSSSSSXXX",
                    Locale.getDefault()
                )
                val outputFormat = SimpleDateFormat("dd MMM yy hh:mm a", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val inputDate = inputFormat.parse(time)

                view.text = outputFormat.format(inputDate!!)
            } else {
                view.text = "unknown time"
            }

        }
    }

}