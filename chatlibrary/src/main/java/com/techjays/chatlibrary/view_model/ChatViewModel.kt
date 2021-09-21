package com.techjays.chatlibrary.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.techjays.chatlibrary.api.AppServices
import com.techjays.chatlibrary.api.Response
import com.techjays.chatlibrary.api.ResponseListener

/**
 * Created by Mathan on 31/08/21.
 **/
class ChatViewModel(private val mContext: Context) : ViewModel(), ResponseListener {

    private var chat = MutableLiveData<Response>()

    fun getChatList(offset: Int, limit: Int) {
        AppServices.getChatList(mContext, offset, limit, this)
    }

    fun getChatMessage(offset: Int, limit: Int, userId:String) {
        AppServices.getChatMessage(mContext, offset, limit,userId, this)
    }

    fun getChatObserver(): MutableLiveData<Response> {
        return chat
    }

    override fun onResponse(r: Response?) {
        try {
            if (r != null) {
                when(r.requestType){
                    AppServices.API.chat_list.hashCode()-> chat.value = r!!
                    AppServices.API.get_chat_message.hashCode()->chat.value =r!!
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}