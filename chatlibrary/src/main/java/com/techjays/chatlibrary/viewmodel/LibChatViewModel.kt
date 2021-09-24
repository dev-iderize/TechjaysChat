package com.techjays.chatlibrary.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.techjays.chatlibrary.api.LibAppServices
import com.techjays.chatlibrary.api.Response
import com.techjays.chatlibrary.api.ResponseListener

/**
 * Created by Mathan on 31/08/21.
 **/
class LibChatViewModel(private val mContext: Context) : ViewModel(), ResponseListener {

    private var chat = MutableLiveData<Response?>()

    fun getChatList(offset: Int, limit: Int) {
        LibAppServices.getChatList(mContext, offset, limit, this)
    }

    fun getChatMessage(offset: Int, limit: Int, userId: String) {
        LibAppServices.getChatMessage(mContext, offset, limit, userId, this)
    }

    fun getChatObserver(): MutableLiveData<Response?> {
        return chat
    }

    fun deleteChats(ids: String) {
        LibAppServices.deleteChats(mContext, ids, this)
    }

    override fun onResponse(r: Response?) {
        try {
            if (r != null) {
                when (r.requestType) {
                    LibAppServices.API.chat_list.hashCode() -> chat.value = r
                    LibAppServices.API.get_chat_message.hashCode() -> chat.value = r
                    LibAppServices.API.delete_chats.hashCode() -> chat.value = r
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}