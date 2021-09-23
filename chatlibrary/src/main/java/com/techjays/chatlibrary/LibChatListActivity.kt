package com.techjays.chatlibrary

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.techjays.chatlibrary.Util.AppDialogs
import com.techjays.chatlibrary.Util.EndlessRecyclerViewScrollListener
import com.techjays.chatlibrary.api.LibAppServices.API.chat_list
import com.techjays.chatlibrary.api.LibAppServices.API.delete_chats
import com.techjays.chatlibrary.base.LibBaseActivity
import com.techjays.chatlibrary.chat.LibChatActivity
import com.techjays.chatlibrary.model.LibChatList
import com.techjays.chatlibrary.model.LibUser
import com.techjays.chatlibrary.view_model.LibChatViewModel
import kotlin.collections.ArrayList

class LibChatListActivity : LibBaseActivity(), LibChatListAdapter.Callback,
    View.OnClickListener {

    private lateinit var mRecyclerView: RecyclerView
    var mOffset = 0
    var mLimit = 6
    var isNextLink = false
    private lateinit var mListener: EndlessRecyclerViewScrollListener
    private lateinit var mSwipe: SwipeRefreshLayout
    private lateinit var mLibChatViewModel: LibChatViewModel
    var mData = ArrayList<LibChatList>()
    private lateinit var mListAdapterLib: LibChatListAdapter

    private lateinit var mDelete: ImageView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lib_activity_chat_list)

        try {
            val data = intent
            val base_url = data.getStringExtra("base_url").toString()
            val chat_token = data.getStringExtra("chat_token").toString()
            val auth_token = data.getStringExtra("auth_token").toString()
            val userData =
                Gson().fromJson(data.getStringExtra("user_data").toString(), LibUser::class.java)

            if (data.extras!!.containsKey("chat_user_data")) {
                val chatUserData =
                    Gson().fromJson(
                        data.getStringExtra("chat_user_data").toString(),
                        LibUser::class.java
                    )
                val chatData = LibChatList()
                chatData.mCompanyName = chatUserData.mUserName
                chatData.mToUserId = chatUserData.mUserId.toString()
                initChatMessage(chatData)
            }

            ChatLibrary.instance.auth_token = auth_token
            ChatLibrary.instance.chat_token = chat_token
            ChatLibrary.instance.base_url = base_url
            ChatLibrary.instance.libUserData = userData
        } catch (e: Exception) {
            Log.d("ex", e.toString())
            throw  e
        }

        init()
    }

    override fun init() {
        mLibChatViewModel = LibChatViewModel(this)
        mRecyclerView = findViewById(R.id.recycler_chat_list)
        mSwipe = findViewById(R.id.chat_swipe)
        mDelete = findViewById(R.id.delete_button)
        initRecycler()
        getChatList(true)
        initObserver()
        clickListener()
    }

    private fun initObserver() {
        if (!mLibChatViewModel.getChatObserver().hasActiveObservers()) {
            mLibChatViewModel.getChatObserver().observe(this, {
                AppDialogs.hideProgressDialog()
                mSwipe.isRefreshing = false
                when (it?.requestType) {
                    chat_list.hashCode() -> {
                        if (it.responseStatus!!) {
                            isNextLink = (it as LibChatList).mNextLink
                            if (mOffset == 0)
                                mData.clear()
                            mData.addAll(it.mData)
                            mListAdapterLib.notifyDataSetChanged()
                        } else
                            AppDialogs.customOkAction(this, it.responseMessage)
                    }

                    delete_chats.hashCode() -> {
                        if (it.responseStatus!!) {
                            val iterator = mData.iterator()
                            while (iterator.hasNext()) {
                                val item = iterator.next()
                                if (item.isChecked) {
                                    iterator.remove()
                                }
                            }
                            mListAdapterLib.notifyDataSetChanged()
                        } else AppDialogs.showSnackbar(mRecyclerView, it.responseMessage)
                    }
                }
            })
        }
    }


    private fun initRecycler() {

        val layoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = layoutManager
        mListAdapterLib = LibChatListAdapter(this, mData, this)
        mRecyclerView.adapter = mListAdapterLib

        mListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                try {
                    if (isNextLink) {
                        mOffset += mLimit
                        getChatList(false)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
        mRecyclerView.addOnScrollListener(mListener)

    }

    private fun getChatList(show: Boolean) {
        mSwipe.isRefreshing = !checkInternet()
        if (checkInternet()) {
            if (show)
                AppDialogs.showProgressDialog(this)
            mLibChatViewModel.getChatList(mOffset, mLimit)
        }
    }


    override fun clickListener() {
        mSwipe.setOnRefreshListener {
            mListener.resetState()
            mOffset = 0
            getChatList(false)
        }
        mDelete.setOnClickListener(this)
    }

    override fun initChatMessage(selectedLibChat: LibChatList) {
        val i = Intent(this, LibChatActivity::class.java)
        i.putExtra("chat_user", selectedLibChat)
        startActivity(i)
    }

    override fun initDelete() {
        mDelete.visibility = if (mDelete.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    override fun onClick(view: View) {
        if (view == mDelete) {
            val id = ArrayList<String>()
            for (i in mData) {
                if (i.isChecked)
                    id.add(i.mToUserId)
            }
            if (id.isNotEmpty())
                mLibChatViewModel.deleteChats(TextUtils.join(",", id))
            else AppDialogs.showSnackbar(mDelete, "Please select something!")
        }
    }
}