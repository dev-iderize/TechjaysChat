package com.techjays.chatlibrary.model

import androidx.databinding.BaseObservable
import com.google.gson.annotations.SerializedName
import com.techjays.chatlibrary.api.Response
import java.io.Serializable

class GroupInfo : Response() {

    @SerializedName("data")
    var mData: GroupInfoData? = null

    class GroupInfoData : Serializable, BaseObservable() {

        @SerializedName("group_id")
        var mGroupId: Int = -1

        @SerializedName("group_name")
        var mGroupName: String = ""

        @SerializedName("created_by_id")
        var mCreatorId: Int = -1

        @SerializedName("creator_phone_number")
        var mPhoneNumber: String = ""

        @SerializedName("full_name")
        var mName: String = ""

        @SerializedName("display_picture")
        var mDisplayPicture: String = ""

        @SerializedName("file_type")
        var mFileType: String = ""

        @SerializedName("medium_image")
        var mMediumImage: String = ""

    }
}