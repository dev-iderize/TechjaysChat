package com.techjays.chatlibrary.model

import androidx.databinding.BaseObservable
import com.google.gson.annotations.SerializedName
import com.techjays.chatlibrary.api.Response
import java.io.Serializable

class ChatList : Response() {

    @SerializedName("data")
    var mData = ArrayList<ChatListData>()

    class ChatListData : Serializable, BaseObservable() {

        @SerializedName("group_id")
        var mGroupId: Int = -1

        @SerializedName("group_name")
        var mGroupName: String = ""

        @SerializedName("message_id")
        var mMessageId: Int = -1

        @SerializedName("creator_id")
        var mCreatorId: Int = -1

        @SerializedName("message")
        var mMessage: String = ""

        @SerializedName("message_type")
        var mMessageType: String = ""

        @SerializedName("timestamp")
        var mTime: String = ""

        @SerializedName("display_picture")
        var mDisplayPicture: String = ""

        @SerializedName("is_sent_by_myself")
        var isSentByMyself: Boolean = false

        @SerializedName("last_read_timestamp")
        var mLastSentMsgTimeStamp: String = ""

        @SerializedName("file_type")
        var mFileType: String = ""

        @SerializedName("medium_image")
        var mMediumImage: String = ""

        @SerializedName("is_read")
        var mIsRead: Boolean = false

    }
}

