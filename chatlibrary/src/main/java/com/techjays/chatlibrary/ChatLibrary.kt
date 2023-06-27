package com.techjays.chatlibrary


/**
 * Created by kishore on 21/Sep/2021.
 */
class ChatLibrary private constructor() {

    var baseUrl = "https://stg-api.shieldup.ai/api/portal/"
    var socketUrl = ""
    var chatToken = ""
    var authToken = ""
    var mUserId: Int = -1
    var mPhoneNumber: String = ""
    var mColor = ""

    companion object {
        var instance = ChatLibrary()
    }
}