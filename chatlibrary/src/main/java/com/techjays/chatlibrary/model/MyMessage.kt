package com.techjays.chatlibrary.model

import com.google.gson.annotations.SerializedName

data class MyMessage(
    @SerializedName("result") val result: Boolean,
    @SerializedName("type") val type: String,
    @SerializedName("chat_type") val chatType: String,
    @SerializedName("msg") val msg: String,
    @SerializedName("data") val data: ChatData
) {
    data class ChatData(
        @SerializedName("group_id") val groupId: Int,
        @SerializedName("message_id") val messageId: Int,
        @SerializedName("message_type") val messageType: String,
        @SerializedName("phone_number") var mPhoneNumber: String = "",
        @SerializedName("message") val message: String,
        @SerializedName("file_type") val fileType: String,
        @SerializedName("medium_image") val mediumImage: String,
        @SerializedName("thumbnail_image") val thumbnailImage: String,
        @SerializedName("timestamp") val timestamp: String
    )
}