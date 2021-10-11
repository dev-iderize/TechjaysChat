package com.techjays.chatlibrary.model

import com.google.gson.annotations.SerializedName
import com.techjays.chatlibrary.api.Response

/**
 * Created by Srinath on 21/09/21.
 **/

class LibChatMessages : Response() {



    @SerializedName("next_link")
    var mNextLink = false

    @SerializedName("data")
    var mData = ArrayList<LibChatMessages>()

    @SerializedName("message_id")
    var mMessageId = ""

    @SerializedName("is_sent_by_myself")
    var mIsSentByMyself = false

    @SerializedName("message")
    var mMessage = ""

    @SerializedName("timestamp")
    var mTimeStamp = ""

    @SerializedName("to_user_id")
    var mToUserId = ""

    var isChecked: Boolean = false
    var showCheckBox: Boolean = false
    var deleteTypeForAll:Boolean=false
}