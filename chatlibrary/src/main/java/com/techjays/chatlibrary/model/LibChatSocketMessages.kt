package com.techjays.chatlibrary.model

import com.google.gson.annotations.SerializedName
import com.techjays.chatlibrary.api.Response

/**
 * Created by Mathan on 22/09/21.
 **/

open class LibChatSocketMessages : Response() {

    @SerializedName("type")
    var mType = ""

    @SerializedName("chat_type")
    var mChatType = ""

    @SerializedName("data")
    var mData: LibChatSocketMessages? = null

    @SerializedName("sender")
    var mSender: LibChatSocketMessages? = null

    @SerializedName("user_id")
    var mUserId = -1

    @SerializedName("username")
    var mUserName = ""

    @SerializedName("profile_pic")
    var mProfilePic = ""

    @SerializedName("message_id")
    var mMessageId = ""

    @SerializedName("message_type")
    var mMessageType = ""

    @SerializedName("file_type")
    var mFileType = ""

    @SerializedName("message")
    var mMessage = ""

    @SerializedName("medium_image")
    var mFileMediumThumbNail = ""

    @SerializedName("thumbnail_image")
    var mFileThumbNail = ""

    @SerializedName("duel_id")
    var mDuelId = 0

    @SerializedName("timestamp")
    var mTimeStamp = ""

    //old model
    @SerializedName("file_url")
    var mFile = ""

    @SerializedName("file_name")
    var mFileName = ""

    @SerializedName("event_name")
    var mEventName = ""
}