package com.techjays.chatlibrary


/**
 * Created by kishore on 21/Sep/2021.
 */
class ChatLibrary private constructor() {

    var base_url = ""
    var chat_token = ""
    var auth_token = ""

    companion object {
        // Getter-Setters
        var instance = ChatLibrary()

    }
}