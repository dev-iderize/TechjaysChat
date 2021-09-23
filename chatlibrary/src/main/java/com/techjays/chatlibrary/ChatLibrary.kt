package com.techjays.chatlibrary

import com.techjays.chatlibrary.model.LibUser


/**
 * Created by kishore on 21/Sep/2021.
 */
class ChatLibrary private constructor() {

    var base_url = ""
    var chat_token = ""
    var auth_token = ""
    var libUserData: LibUser? = null

    companion object {
        // Getter-Setters
        var instance = ChatLibrary()

    }
}