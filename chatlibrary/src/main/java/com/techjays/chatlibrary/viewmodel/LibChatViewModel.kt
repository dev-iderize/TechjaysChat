package com.techjays.chatlibrary.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.techjays.chatlibrary.api.LibAppServices
import com.techjays.chatlibrary.api.Response
import com.techjays.chatlibrary.api.ResponseListener
import com.techjays.chatlibrary.model.LibChatMessages
import com.techjays.chatlibrary.util.AppDialogs

/**
 * Created by Mathan on 31/08/21.
 **/
class LibChatViewModel(private val mContext: Context) : ViewModel(), ResponseListener {

    private var chat = MutableLiveData<Response?>()
    private var file = MutableLiveData<Response?>()
    private var image = MutableLiveData<Response?>()

    fun getChatList(offset: Int, limit: Int) {
        LibAppServices.getChatList(mContext, offset, limit, this)
    }

    fun getChatMessage(offset: Int, limit: Int, userId: String,duelId:String) {
        LibAppServices.getChatMessage(mContext, offset, limit, userId,duelId, this)
    }

    fun getChatObserver(): MutableLiveData<Response?> {
        return chat
    }

    fun getChatFileObserver(): MutableLiveData<Response?> {
        return file
    }

    fun getChatImageObserver(): MutableLiveData<Response?> {
        return image
    }

    fun deleteChats(ids: String) {
        LibAppServices.deleteChats(mContext, ids, this)
    }

    fun uploadFile(chatMessages: LibChatMessages) {
        LibAppServices.fileUpload(mContext, chatMessages, this)
    }

    fun uploadImageVideo(path:String,type:String) {
        LibAppServices.mImageVideoUpload(mContext, path,type, this)
    }

    fun deleteMessages(Userid: Int, isforme: Boolean, ids: String, duelId:String) {
        LibAppServices.deleteMessages(mContext, Userid, isforme, ids,duelId, this)
    }


    override fun onResponse(r: Response?) {
        try {
            if (r != null) {
                when (r.requestType) {
                    LibAppServices.API.chat_list.hashCode() -> chat.value = r
                    LibAppServices.API.get_chat_message.hashCode() -> chat.value = r
                    LibAppServices.API.delete_chats.hashCode() -> chat.value = r
                    LibAppServices.API.delete_messages.hashCode() -> chat.value = r
                    LibAppServices.API.upload_file.hashCode() -> file.value = r
                    LibAppServices.API.upload_image.hashCode() ->{
                        image.value = r
                        AppDialogs.hideProgressDialog()
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}