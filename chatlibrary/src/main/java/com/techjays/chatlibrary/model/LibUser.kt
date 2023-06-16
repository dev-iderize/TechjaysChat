package com.techjays.chatlibrary.model

import com.google.gson.annotations.SerializedName
import com.techjays.chatlibrary.api.Response

class User : Response() {

    @SerializedName("id")
    var mId = -1

    @SerializedName("data")
    var mUser: User? = null

    @SerializedName("token")
    var mUserToken = ""

    @SerializedName("mobile_number")
    var mobile_number = ""

    @SerializedName("full_name")
    var full_name = ""

    @SerializedName("first_name")
    var first_name = ""

    @SerializedName("last_name")
    var last_name = ""

    @SerializedName("country_code")
    var country_code = ""

}