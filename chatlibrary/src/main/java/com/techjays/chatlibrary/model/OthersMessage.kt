package com.techjays.chatlibrary.model

import com.google.gson.annotations.SerializedName

data class OthersMessage(
    @SerializedName("result") val result: Boolean,
    @SerializedName("msg") val msg: String,
    @SerializedName("type") val type: String,
    @SerializedName("chat_type") val chatType: String,
    @SerializedName("data") val data: ChatData
) {
    data class ChatData(
        @SerializedName("sender") val sender: Sender,
        @SerializedName("profile_pic") val profilePic: String,
        @SerializedName("group_id") val groupId: Int,
        @SerializedName("message_id") val messageId: Int,
        @SerializedName("message_type") val messageType: String,
        @SerializedName("message") val message: String,
        @SerializedName("file_type") val fileType: String,
        @SerializedName("medium_image") val mediumImage: String,
        @SerializedName("thumbnail_image") val thumbnailImage: String,
        @SerializedName("timestamp") val timestamp: String
    )

    data class Sender(
        @SerializedName("user_id") val userId: Int,
        @SerializedName("username") val username: String,
        @SerializedName("first_name") val firstName: String,
        @SerializedName("last_name") val lastName: String,
        @SerializedName("profile_image") val profileImage: String,
        @SerializedName("profile_thumbnail_image") val profileThumbnailImage: String
    )
}
