package com.techjays.chatlibrary

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context


class ChatLibrary : Application() {

    var base_url = ""
    var chat_token = ""
    var auth_token = ""

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()
        mContext = this

    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null

        fun instance(): ChatLibrary {
            return mContext!!.applicationContext as ChatLibrary
        }
    }
}