package com.techjays.chatlibrary.util

import android.content.res.ColorStateList
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.text.Spannable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.github.curioustechizen.ago.RelativeTimeTextView
import com.google.android.material.imageview.ShapeableImageView
import com.santalu.aspectratioimageview.AspectRatioImageView
import com.squareup.picasso.Picasso
import com.techjays.chatlibrary.R
import de.hdodenhof.circleimageview.CircleImageView


class DataBidingAdapter {
    companion object {


        @JvmStatic
        @BindingAdapter("seekBarProgressBackgroundTint")
        fun setSeekBarProgressBackgroundTint(seekBar: SeekBar, isSentByMyself: Boolean) {
            val progressBackgroundTint = if (isSentByMyself) {
                ContextCompat.getColor(seekBar.context, R.color.primary_color_light)
            } else {
                ContextCompat.getColor(seekBar.context, R.color.white)
            }
            seekBar.progressBackgroundTintList = ColorStateList.valueOf(progressBackgroundTint)
        }


        @JvmStatic
        @BindingAdapter("chat_info_message")
        fun setChatInfoMessage(
            textView: TextView,
            message: com.techjays.chatlibrary.model.Chat.ChatData
        ) {
            val mNotificationType = when {
                message.mMessage.contains("turned on their Shield") -> "SHIELD_ON"

                message.mMessage.contains("turned off their Shield") -> "SHIELD_OFF"

                message.mMessage.contains("turned off their SOS") -> "SOS_OFF"

                message.mMessage.contains("triggered SOS") -> "SOS_ON"
                else -> "I_AM_SAFE"
            }
            textView.text = Utility.setChatNotification(
                message.mMessage,
                Utility.displayLocalTime(message.mTime),
                Utility.notificationColor(mNotificationType),
                textView.context.applicationContext
            )
        }


        @JvmStatic
        @BindingAdapter("set_relative_date")
        fun setRelativeDate(view: TextView, aData: String) {
            view.text = DateUtil.convertTimeToTextExact(aData)
        }

        @JvmStatic
        @BindingAdapter("set_circle_image")
        fun setCircleImage(view: CircleImageView, aData: String) {
            if (aData.isNotEmpty())
                Picasso.get().load(aData).placeholder(R.drawable.ic_user_icon_white).into(view)
        }

        @JvmStatic
        @BindingAdapter("set_image")
        fun setImage(view: AspectRatioImageView, url: String?) {
            if (url != null)
                Glide.with(view.context.applicationContext).load(url).into(view)
            else
                view.setImageResource(R.drawable.ic_pause_icon)
        }

        @JvmStatic
        @BindingAdapter("set_aspect_ratio")
        fun setAspectRatio(view: AspectRatioImageView, ratio: Float) {
            view.ratio = ratio

        }


        @BindingAdapter("load_image")
        @JvmStatic
        fun loadImage(view: ShapeableImageView, url: String?) {
            if (!url.isNullOrEmpty()) {
                Utility.loadUserImage(
                    url,
                    view,
                    R.drawable.ic_acc
                )

            } else {
                val aPlaceholder =
                    "https://force-field-dev.s3.amazonaws.com/common/images/default_user_image.png"
                Picasso.get().load(aPlaceholder).error(R.drawable.ic_acc).fit()
                    .centerCrop().into(view)
            }
        }
    }
}