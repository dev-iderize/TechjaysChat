package com.techjays.chatlibrary.model

import com.google.gson.annotations.SerializedName
import com.techjays.chatlibrary.api.Response

/**
 * Created by Srinath on 17/09/21.
 **/


class LibChatList: Response() {

    @SerializedName("next_link")
    var mNextLink = false

    @SerializedName("data")
    var mData = ArrayList<LibChatList>()

    @SerializedName("message_id")
    var mMessageId = ""

    @SerializedName("is_sent_by_myself")
    var misSentByMyself = false

    @SerializedName("message")
    var mMessage = ""

    @SerializedName("timestamp")
    var mTimeStamp = ""

    @SerializedName("to_user_id")
    var mToUserId = ""

    @SerializedName("first_name")
    var mFirstName = ""

    @SerializedName("company_name")
    var mCompanyName = ""

    @SerializedName("user_type")
    var mUserType = ""

    @SerializedName("profile_pic")
    var mProfilePic = ""

    var isChecked = false
    var showCheckBox = false

    var newMessage:Boolean = false
}