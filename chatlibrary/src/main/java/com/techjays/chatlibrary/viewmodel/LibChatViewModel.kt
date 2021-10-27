package com.techjays.chatlibrary.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.techjays.chatlibrary.api.LibAppServices
import com.techjays.chatlibrary.api.Response
import com.techjays.chatlibrary.api.ResponseListener
import com.techjays.chatlibrary.model.LibChatMessages

/**
 * Created by Mathan on 31/08/21.
 **/
class LibChatViewModel(private val mContext: Context) : ViewModel(), ResponseListener {

    private var chat = MutableLiveData<Response?>()
    private var file = MutableLiveData<Response?>()

    fun getChatList(offset: Int, searchterm:String,limit: Int) {
        LibAppServices.getChatList(mContext, offset, limit,searchterm, this)
    }

    fun getChatMessage(offset: Int, limit: Int, userId: String) {
        LibAppServices.getChatMessage(mContext, offset, limit, userId, this)
    }

    fun getChatObserver(): MutableLiveData<Response?> {
        return chat
    }

    fun getChatFileObserver(): MutableLiveData<Response?> {
        return file
    }

    fun deleteChats(ids: String) {
        LibAppServices.deleteChats(mContext, ids, this)
    }

    fun uploadFile(chatMessages: LibChatMessages) {
        LibAppServices.fileUpload(mContext, chatMessages, this)
    }

    fun deleteMessages(Userid: Int, isforme: Boolean, ids: String) {
        LibAppServices.deleteMessages(mContext, Userid, isforme, ids, this)
    }


    override fun onResponse(r: Response?) {
        try {
            if (r != null) {
                when (r.requestType) {
                    LibAppServices.API.chat_list.hashCode() -> chat.value = r
                    LibAppServices.API.searchlist.hashCode() -> chat.value = r
                    LibAppServices.API.get_chat_message.hashCode() -> chat.value = r
                    LibAppServices.API.delete_chats.hashCode() -> chat.value = r
                    LibAppServices.API.delete_messages.hashCode() -> chat.value = r
                    LibAppServices.API.upload_file.hashCode() -> file.value = r
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}