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

    @SerializedName("message_type")
    var mMessageType = ""

    @SerializedName("file_type")
    var mFileType = ""

    @SerializedName("message")
    var mMessage = ""

    @SerializedName("timestamp")
    var mTimeStamp = ""

    @SerializedName("medium_image")
    var mFileMediumThumbNail = ""

    @SerializedName("thumbnail_image")
    var mFileThumbNail = ""


    //old model

    @SerializedName("to_user_id")
    var mToUserId = ""

    @SerializedName("file")
    var mFile = ""

    var isChecked: Boolean = false
    var showCheckBox: Boolean = false
}