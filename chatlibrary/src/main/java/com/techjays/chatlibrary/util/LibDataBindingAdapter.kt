package com.techjays.chatlibrary.util

import android.content.res.ColorStateList
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.santalu.aspectratioimageview.AspectRatioImageView
import com.squareup.picasso.Picasso
import com.techjays.chatlibrary.ChatLibrary
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.model.ChatList
import de.hdodenhof.circleimageview.CircleImageView


class LibDataBidingAdapter {
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
                message.mMessage.contains("turned on") && message.mMessage.contains("Shield") -> "SHIELD_ON"

                message.mMessage.contains("turned off") && message.mMessage.contains("Shield") -> "SHIELD_OFF"

                message.mMessage.contains("auto-triggered") -> "SOS_AUTO_TRIGGER"

                message.mMessage.contains("turned off") && message.mMessage.contains("SOS") -> "SOS_OFF"

                message.mMessage.contains("triggered") && message.mMessage.contains("SOS") -> "SOS_ON"
                else -> "I_AM_SAFE"
            }
            if (message.mPhoneNumber.contains(ChatLibrary.instance.mPhoneNumber)) {
                message.mMessage = when (mNotificationType) {
                    "SHIELD_ON" -> "You turned on your Shield"
                    "SHIELD_OFF" -> "You turned off your Shield"
                    "SOS_ON" -> "You triggered your SOS"
                    "SOS_AUTO_TRIGGER" -> "Your SOS was auto-triggered"
                    "SOS_OFF" -> "You turned off your SOS"
                    "I_AM_SAFE" -> "You checked in as safe"
                    else -> ""
                }
            }


            textView.text = LibChatUtility.setChatNotification(
                LibChatUtility.replaceContactName(
                    message.mMessage,
                    message.mPhoneNumber,
                    textView.context.applicationContext
                ),
                LibChatUtility.displayLocalTime(message.mTime),
                LibChatUtility.notificationColor(mNotificationType),
                textView.context.applicationContext
            )
        }


        @JvmStatic
        @BindingAdapter("set_relative_date")
        fun setRelativeDate(view: TextView, aData: String) {
            view.text = DateUtil.convertTimeToTextExact(aData)
        }

        @JvmStatic
        @BindingAdapter("view_visibility")
        fun setViewVisibility(view: TextView, aData: ChatList.ChatListData) {
            val viewCondition =
                aData.mMessage != null && aData.mMessage.isNotEmpty() && aData.mMessageType != "file"
            view.visibility = if (viewCondition) View.VISIBLE else View.GONE
        }

        @JvmStatic
        @BindingAdapter(value = ["myDrawableStart", "myDrawableTint"], requireAll = false)
        fun setDrawableStart(textView: TextView, aData: ChatList.ChatListData, tint: Int) {
            if (aData.mMessage != "" && aData.mMessage != null) {

                val drawable = LibChatUtility.getDrawable(
                    textView.context.applicationContext,
                    LibChatUtility.findSuitableDrawables(aData.mFileType)
                )

                val tintedDrawable = if (tint != 0) {
                    drawable?.mutate()?.apply {
                        DrawableCompat.setTint(this, tint)
                    }
                } else {
                    drawable
                }

                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    tintedDrawable,
                    null,
                    null,
                    null
                )
            } else {
                textView.visibility = View.GONE
            }
        }


        @JvmStatic
        @BindingAdapter("myText")
        fun capitalizeFirstLetter(textView: TextView, data: ChatList.ChatListData) {
            if (data.mFileType != "message") {
                val inputString = data.mFileType.replaceFirstChar { it.uppercase() }
                textView.text = inputString
            } else {
                try {
                    val mNotificationType = when {
                        data.mMessage.contains("turned on") && data.mMessage.contains("Shield") -> "SHIELD_ON"

                        data.mMessage.contains("turned off") && data.mMessage.contains("Shield") -> "SHIELD_OFF"

                        data.mMessage.contains("auto-triggered") -> "SOS_AUTO_TRIGGER"

                        data.mMessage.contains("turned off") && data.mMessage.contains("SOS") -> "SOS_OFF"

                        data.mMessage.contains("triggered") && data.mMessage.contains("SOS") -> "SOS_ON"
                        else -> "I_AM_SAFE"
                    }
                    if (data.mPhoneNumber.contains(ChatLibrary.instance.mPhoneNumber)) {
                        data.mMessage = when (mNotificationType) {
                            "SHIELD_ON" -> "You turned on your Shield"
                            "SHIELD_OFF" -> "You turned off your Shield"
                            "SOS_ON" -> "You triggered your SOS"
                            "SOS_AUTO_TRIGGER" -> "Your SOS was auto-triggered"
                            "SOS_OFF" -> "You turned off your SOS"
                            "I_AM_SAFE" -> "You checked in as safe"
                            else -> ""
                        }
                    }
                    textView.text = LibChatUtility.replaceContactName(
                        data.mMessage,
                        data.mPhoneNumber,
                        textView.context.applicationContext
                    )
                } catch (e: Exception) {

                    textView.text = LibChatUtility.replaceContactName(
                        data.mMessage,
                        data.mPhoneNumber,
                        textView.context.applicationContext
                    )
                }
            }
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
                LibChatUtility.loadUserImage(
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