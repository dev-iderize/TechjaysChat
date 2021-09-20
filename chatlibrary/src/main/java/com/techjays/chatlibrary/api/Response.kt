package com.techjays.chatlibrary.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Response: Serializable {

    @SerializedName("result")
    var responseStatus: Boolean? = null

    @SerializedName("msg")
    var responseMessage = ""

    var requestType: Int? = null
}