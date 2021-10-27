package com.techjays.chatlibrary.model

import com.google.gson.annotations.SerializedName
import com.techjays.chatlibrary.api.Response

/**
 * Created by Mathan on 27/10/20.
 **/

class Follow : Response() {

    @SerializedName("next_link")
    var mNextLink = false

    @SerializedName("data")
    var mData = ArrayList<Follow>()

    @SerializedName("to_user_id")
    var mUserId = ""

    @SerializedName("first_name")
    var mUserFirstName = ""

    @SerializedName("last_name")
    var mUserLastName = ""

    @SerializedName("username")
    var mUserName = ""

    @SerializedName("profile_pic")
    var mUserPicture = ""

    @SerializedName("medium_profile_pic")
    var mMediumPicture = ""

    @SerializedName("thumbnail_profile_pic")
    var mThumbPicture = ""

    @SerializedName("is_following")
    var isFollowing: Boolean? = null
}
