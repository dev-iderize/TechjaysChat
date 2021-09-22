package com.techjays.chatlibrary.Util

import android.util.Log
import com.google.gson.Gson
import com.techjays.chatlibrary.ChatLibrary
import com.techjays.chatlibrary.model.ChatSocketMessages
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class ChatSocketListener(private var mCallback: CallBack) : WebSocketListener() {

    private lateinit var ws: WebSocket

    override fun onOpen(webSocket: WebSocket, response: Response?) {
        Log.e("Opened:", "Successfully")
        ws = webSocket
        ws.send(getConnectionParams())

    }

    fun getConnectionParams(): String {
        return "{\n" +
                "\n" +
                "    \"token\": \"${ChatLibrary.instance.chat_token}\",\n" +
                "\n" +
                "    \"type\": \"connect\"\n" +
                "\n" +
                "}"
    }

    fun sendChatParams(msg: String, to: String): String {
        return "{\n" +
                "    \"token\": \"${ChatLibrary.instance.chat_token}\",\n" +
                "    \"type\": \"chat\",\n" +
                "    \"chat_type\": \"private\",\n" +
                "    \"to\": $to,\n" +
                "    \"message\": \"$msg\"\n" +
                "}"
    }

    override fun onMessage(webSocket: WebSocket?, text: String) {
        Log.e("Receiving:", " $text")
        if (text.isNotEmpty()) {
            val receivedNewMessage = Gson().fromJson(text, ChatSocketMessages::class.java)
            if (receivedNewMessage.mChatType == "chat") {
                receivedNewMessage.mTimeStamp = (System.currentTimeMillis() / 1000).toString()
                mCallback.onMessageReceive(receivedNewMessage)
            }
        }
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
        Log.e("Receiving bytes : ", bytes.hex())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        Log.e("Closing ", "$code / $reason")
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable, response: Response?) {
        t.message?.let { Log.e("Error : ", it) }
    }

    fun sendChat(s: String, mToUserId: String) {
        Log.e("sent", sendChatParams(s, mToUserId))
        ws.send(sendChatParams(s, mToUserId))
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }

    interface CallBack {
        fun onMessageReceive(chatMessage: ChatSocketMessages)
    }
}