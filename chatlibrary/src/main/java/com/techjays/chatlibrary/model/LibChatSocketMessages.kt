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

    @SerializedName("message_id")
    var mMessageId = ""

    @SerializedName("data")
    var mData: LibChatSocketMessages? = null

    @SerializedName("message")
    var mMessage = ""

    @SerializedName("sender")
    var mSender: LibChatSocketMessages? = null

    @SerializedName("user_id")
    var mUserId = -1

    @SerializedName("username")
    var mUserName = ""

    @SerializedName("timestamp")
    var mTimeStamp = ""

    @SerializedName("profile_pic")
    var mProfilePic = ""

    @SerializedName("file_url")
    var mFile = ""
}