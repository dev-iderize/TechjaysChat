package com.techjays.chatlibrary.model

import androidx.databinding.BaseObservable
import com.google.gson.annotations.SerializedName
import com.techjays.chatlibrary.api.Response
import java.io.Serializable

class Chat : Response() {

    @SerializedName("data")
    var mData = ArrayList<ChatData>()

    class ChatData : Serializable, BaseObservable() {

        @SerializedName("message_id")
        var mMessageId: Int = -1

        @SerializedName("message_type")
        var mMessageType: String = ""

        @SerializedName("file_type")
        var mFileType: String = ""

        @SerializedName("medium_image")
        var mMediumImage: String = ""

        @SerializedName("thumbnail_image")
        var mThumbnailImage: String = ""

        @SerializedName("file_url")
        var mFileUrl: String = ""

        @SerializedName("message")
        var mMessage: String = ""

        @SerializedName("timestamp")
        var mTime: String = ""

        @SerializedName("profile_img")
        var mProfilePic: String = ""

        @SerializedName("phone_number")
        var mPhoneNumber: String = ""

        @SerializedName("first_name")
        var mFirstName: String = ""

        @SerializedName("last_name")
        var mLastName: String = ""

        var mName = "$mFirstName $mLastName"

        @SerializedName("user_id")
        var mUserId: Int = -1

        @SerializedName("is_sent_by_myself")
        var isSentByMyself: Boolean = false

        @SerializedName("last_read_timestamp")
        var mLastSentMsgTimeStamp: String = ""

        @SerializedName("is_read")
        var mIsRead: Boolean = false

    }
}

