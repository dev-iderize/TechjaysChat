package com.techjays.chatlibrary.util

import android.content.Context
import android.content.Intent
import android.util.Log
import com.techjays.chatlibrary.ChatLibrary
//import com.techjays.shieldup.app.database.LocalStorageSP
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject

class ChatSocketListener(
    private var mContext: Context,
    private var ws: WebSocket?
) : WebSocketListener() {

    fun initialize(webSocket: WebSocket) {
        ws = webSocket
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        if (ws != null) {

            Log.e("WEB-SOCKET OPENED:", "Success")
            ws!!.send(getConnectionParams())
        } else {
            Log.e("error", "Please call ForcefieldSocketListener.initialize()")
        }
    }

    fun sendChat(s: String, mToUserId: Int, ws: WebSocket) {
        ws.send(sendChatParams(s, mToUserId))
    }

    private fun getConnectionParams(): String {
        val obj = JSONObject()
        obj.put("token", ChatLibrary.instance.chatToken)
        obj.put("type", "connect")

        val userData = JSONObject()
        userData.put(
            "user_id", ChatLibrary.instance.mUserId

        )
        obj.put("user_data", userData)
        Log.e("ConnectonPArams-->", obj.toString())
        return obj.toString()
    }

    private fun sendChatParams(message: String, groupId: Int): String {
        val obj = JSONObject()
        obj.put("type", "chat")
        obj.put("group_id", groupId)
        obj.put("token", ChatLibrary.instance.chatToken)
        obj.put("chat_type", "group")
        obj.put("message_type", "message")
        obj.put("message", message)
        val userData = JSONObject()
        userData.put("user_id", ChatLibrary.instance.mUserId)
        obj.put("user_data", userData)
        Log.d("c", obj.toString())
        return obj.toString()
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.e("Receiving bytes : ", bytes.hex())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        Log.e("Closing ", "$code / $reason")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.e("Receiving:", " $text")
        try {
            if (text.isNotEmpty()) {
                val obj = JSONObject(text)
                val intent = Intent()
                val isSentMyself = !obj.getJSONObject("data").has("sender")
                Log.e("issse", isSentMyself.toString())
                intent.action = "chat_web_socket_message"
                intent.putExtra("type", obj.get("type").toString())
                intent.putExtra("value", text)
                intent.putExtra("isSentMyself", isSentMyself)
                mContext.sendBroadcast(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        t.message?.let {
            Log.e("Error : ", it)
        }

    }

    fun cancel() {
        try {
            Log.e("Cancelled", "weBSocket Cancelled")
            ws?.cancel()
        } catch (_: Exception) {
        }
    }


    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }


}