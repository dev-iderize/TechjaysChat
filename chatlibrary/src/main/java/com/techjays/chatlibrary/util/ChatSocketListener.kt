package com.techjays.chatlibrary.util

import android.util.Log
import com.google.gson.Gson
import com.techjays.chatlibrary.ChatLibrary
import com.techjays.chatlibrary.model.LibChatSocketMessages
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject


class ChatSocketListener(private var mCallback: CallBack) : WebSocketListener() {

    private lateinit var ws: WebSocket

    override fun onOpen(webSocket: WebSocket, response: Response?) {
        Log.e("Opened:", "Successfully")
        ws = webSocket
        ws.send(getConnectionParams())
        Log.d("opeeeeeee", getConnectionParams())

    }

    fun getConnectionParams(): String {

        val obj = JSONObject()
        obj.put("token", ChatLibrary.instance.chatToken)
        obj.put("type", "connect")

        Log.e("connect", obj.toString())

        return obj.toString()
    }

    fun sendChatParams(msg: String, to: String, type: String): String {
        val obj = JSONObject()
        obj.put("token", ChatLibrary.instance.chatToken)
        obj.put("type", "chat")
        obj.put("chat_type", "private")
        obj.put("to", to)
        obj.put("message_type", type)
        obj.put("message", msg)

        Log.e("sent", obj.toString())

        return obj.toString()
    }

    override fun onMessage(webSocket: WebSocket?, text: String) {
        Log.e("Receiving:", " $text")
        try {
            if (text.isNotEmpty()) {
                val obj = JSONObject(text)
                if (obj.get("type").equals("chat")) {
                    val receivedNewMessage =
                        Gson().fromJson(text, LibChatSocketMessages::class.java)
                    if (receivedNewMessage.responseStatus!! && receivedNewMessage.mType == "chat") {
                        if (receivedNewMessage.mData!!.mTimeStamp.isEmpty()) {
                            receivedNewMessage.mData!!.mTimeStamp =
                                DateUtil.getCurrentDataTime(true, "yyyy-MM-dd'T'HH:mm:ss")
                        }

                        mCallback.onMessageReceive(receivedNewMessage)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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

    fun sendChat(s: String, mToUserId: String, type: String) {
        ws.send(sendChatParams(s, mToUserId, type))
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }

    interface CallBack {
        fun onMessageReceive(libChatMessage: LibChatSocketMessages)
    }
}