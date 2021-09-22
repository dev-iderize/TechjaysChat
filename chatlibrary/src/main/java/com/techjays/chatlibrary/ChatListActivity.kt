package com.techjays.chatlibrary

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.techjays.chatlibrary.Util.AppDialogs
import com.techjays.chatlibrary.Util.EndlessRecyclerViewScrollListener
import com.techjays.chatlibrary.Util.Utility
import com.techjays.chatlibrary.api.AppServices.API.chat_list
import com.techjays.chatlibrary.api.AppServices.API.delete_chats
import com.techjays.chatlibrary.base.BaseActivity
import com.techjays.chatlibrary.chat.ChatActivity
import com.techjays.chatlibrary.model.ChatList
import com.techjays.chatlibrary.view_model.ChatViewModel
import java.util.*
import kotlin.collections.ArrayList

class ChatListActivity : BaseActivity(), ChatAdapter.Callback, View.OnClickListener {

    private lateinit var mRecyclerView: RecyclerView
    var mOffset = 0
    var mLimit = 6
    var isNextLink = false
    private lateinit var mListener: EndlessRecyclerViewScrollListener
    private lateinit var mSwipe: SwipeRefreshLayout
    private lateinit var mChatViewModel: ChatViewModel
    var mData = ArrayList<ChatList>()
    private lateinit var mAdapter: ChatAdapter

    private lateinit var mDelete: ImageView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        try {
            val data = intent
            val base_url = data.getStringExtra("base_url").toString()
            val chat_token = data.getStringExtra("chat_token").toString()
            val auth_token = data.getStringExtra("auth_token").toString()

            ChatLibrary.instance.auth_token = auth_token
            ChatLibrary.instance.chat_token = chat_token
            ChatLibrary.instance.base_url = base_url
        } catch (e: Exception) {
            Log.d("ex", e.toString())
            throw  e
        }

        init()
    }

    override fun init() {
        mChatViewModel = ChatViewModel(this)
        mRecyclerView = findViewById(R.id.recycler_chat_list)
        mSwipe = findViewById(R.id.chat_swipe)
        mDelete = findViewById(R.id.delete_button)
        initRecycler()
        getChatList(true)
        initObserver()
        clickListener()
    }

    private fun initObserver() {
        if (!mChatViewModel.getChatObserver().hasActiveObservers()) {
            mChatViewModel.getChatObserver().observe(this, {
                AppDialogs.hideProgressDialog()
                mSwipe.isRefreshing = false
                when (it?.requestType) {
                    chat_list.hashCode() -> {
                        if (it.responseStatus!!) {
                            isNextLink = (it as ChatList).mNextLink
                            if (mOffset == 0)
                                mData.clear()
                            mData.addAll(it.mData)
                            mAdapter.notifyDataSetChanged()
                        } else
                            AppDialogs.customOkAction(this, it.responseMessage)
                    }

                    delete_chats.hashCode() -> {
                        if (it.responseStatus!!) {
                            for (i in mData) {
                                if (i.isChecked)
                                    mData.remove(i)
                            }
                            mAdapter.notifyDataSetChanged()
                        } else AppDialogs.showSnackbar(mRecyclerView, it.responseMessage)
                    }
                }
            })
        }
    }


    private fun initRecycler() {

        val layoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = layoutManager
        mAdapter = ChatAdapter(this, mData, this)
        mRecyclerView.adapter = mAdapter

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
            mChatViewModel.getChatList(mOffset, mLimit)
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

    override fun initChatMessage(selectedChat: ChatList) {
        val i = Intent(this, ChatActivity::class.java)
        i.putExtra("chat_user", selectedChat)
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
                    id.add(i.mMessageId)
            }
            if (id.isNotEmpty())
                mChatViewModel.deleteChats(TextUtils.join(",", id))
            else AppDialogs.showSnackbar(mDelete, "Please select something!")
        }
    }
}