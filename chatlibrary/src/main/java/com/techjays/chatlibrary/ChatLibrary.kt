package com.techjays.chatlibrary

import com.techjays.chatlibrary.model.LibUser


/**
 * Created by kishore on 21/Sep/2021.
 */
class ChatLibrary private constructor() {

    var baseUrl = ""
    var chatToken = ""
    var authToken = ""
    var mUserData: LibUser? = null

    companion object {
        // Getter-Setters
        var instance = ChatLibrary()
    }
}