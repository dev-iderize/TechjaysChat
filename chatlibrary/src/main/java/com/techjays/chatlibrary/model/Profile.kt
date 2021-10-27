package com.techjays.chatlibrary.model

import com.google.gson.annotations.SerializedName
import com.techjays.chatlibrary.api.Response

class Profile : Response() {

    @SerializedName("data")
    var mData: Profile? = null

    @SerializedName("user_id")
    var mUserId = ""

    @SerializedName("first_name")
    var mUserFirstName = ""

    @SerializedName("last_name")
    var mUserLastName = ""

    @SerializedName("username")
    var mUserName = ""

    @SerializedName("hometown")
    var mUserHomeTown = ""

    @SerializedName("bio")
    var mUserBio = ""

    @SerializedName("website")
    var mUserWebsite = ""

    @SerializedName("profile_pic")
    var mUserPicture = ""

    @SerializedName("medium_profile_pic")
    var mMediumPicture = ""

    @SerializedName("thumbnail_profile_pic")
    var mThumbPicture = ""

    @SerializedName("collections_count")
    var mCollections = 0

    @SerializedName("following_count")
    var mFollowing = 0

    @SerializedName("followers_count")
    var mFollowers = 0

    @SerializedName("latitude")
    var mUserLatitude = ""

    @SerializedName("longitude")
    var mUserLongitude = ""

    @SerializedName("is_following")
    var mIsFollowing = false

}