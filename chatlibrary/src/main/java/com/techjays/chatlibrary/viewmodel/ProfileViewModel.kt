package com.techjays.chatlibrary.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.techjays.chatlibrary.api.LibAppServices
import com.techjays.chatlibrary.api.Response
import com.techjays.chatlibrary.api.ResponseListener
import com.techjays.chatlibrary.model.Profile


/**
 * Created by Mathan on 22/10/20.
 **/

class ProfileViewModel(private val mContext: Context) :
    ViewModel(), ResponseListener {
    private var follows = MutableLiveData<Response>()

    /**
     * Followers or Followings users
     */
    fun followsList(
        userId: String,
        searchText: String,
        offset: Int,
        limit: Int,
    ) {
        LibAppServices.getFollows(mContext, userId, searchText, offset, limit, this)
    }

    fun getFollowingsObserver(): MutableLiveData<Response> {
        return follows
    }

    override fun onResponse(r: Response?) {
        try {
            if (r != null) {
               if (
                    r.requestType == LibAppServices.API.following_list.hashCode()
                )
                    follows.value = r!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}