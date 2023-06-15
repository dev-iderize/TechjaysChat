package com.techjays.chatlibrary.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class Response : Serializable {

    @SerializedName("result")
    var responseStatus: Boolean? = null

    @SerializedName("msg")
    var responseMessage = ""

    var requestType: Int? = null

    @SerializedName("next_link")
    var next_link: Boolean = false
}