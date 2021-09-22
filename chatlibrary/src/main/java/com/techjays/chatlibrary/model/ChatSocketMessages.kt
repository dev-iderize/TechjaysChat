package com.techjays.chatlibrary.model

import com.google.gson.annotations.SerializedName
import com.techjays.chatlibrary.api.Response

/**
 * Created by Mathan on 22/09/21.
 **/

class ChatSocketMessages : Response() {

    @SerializedName("type")
    var mType = ""

    @SerializedName("chat_type")
    var mChatType = ""

    @SerializedName("data")
    var mData = ChatSocketMessages()

    @SerializedName("message")
    var mMessage = ""

    @SerializedName("sender")
    var mSender = ChatSocketMessages()

    @SerializedName("user_id")
    var mUserId = -1

    @SerializedName("username")
    var mUserName = ""

    @SerializedName("timestamp")
    var mTimeStamp = ""
}