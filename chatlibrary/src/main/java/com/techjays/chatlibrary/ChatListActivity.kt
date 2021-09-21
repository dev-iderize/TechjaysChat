package com.techjays.chatlibrary

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.techjays.chatlibrary.Util.AppDialogs
import com.techjays.chatlibrary.Util.EndlessRecyclerViewScrollListener
import com.techjays.chatlibrary.base.BaseActivity
import com.techjays.chatlibrary.model.ChatList
import com.techjays.chatlibrary.view_model.ChatViewModel
import java.util.ArrayList

class ChatListActivity : BaseActivity() {

    private lateinit var mRecyclerView: RecyclerView
    var mOffset = 0
    var mLimit = 6
    var isNextLink = false
    private lateinit var mListener: EndlessRecyclerViewScrollListener
    private lateinit var mSwipe: SwipeRefreshLayout
    private lateinit var mChatViewModel: ChatViewModel
    var mData = ArrayList<ChatList>()
    private lateinit var mAdapter: ChatAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        try {
            val data = intent
            val base_url = data.getStringExtra("base_url").toString()
            val chat_token = data.getStringExtra("chat_token").toString()
            val auth_token = data.getStringExtra("auth_token").toString()

            ChatLibrary.instance().auth_token = auth_token
            ChatLibrary.instance().chat_token = chat_token
            ChatLibrary.instance().base_url = base_url
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
        initRecycler()
        getChatList(true)
        clickListener()
    }


    private fun initRecycler() {

        val layoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = layoutManager
        mAdapter = ChatAdapter(this, mData)
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
            if (!mChatViewModel.getChatListObserver().hasActiveObservers()) {
                mChatViewModel.getChatListObserver().observe(this, {
                    AppDialogs.hideProgressDialog()
                    mSwipe.isRefreshing = false
                    if (it.responseStatus!!) {
                        isNextLink = (it as ChatList).mNextLink
                        if (mOffset == 0)
                            mData.clear()
                        mData.addAll(it.mData)
                        mAdapter.notifyDataSetChanged()
                    } else {
                        AppDialogs.customOkAction(this, it.responseMessage)
                        AppDialogs.hideProgressDialog()
                        mSwipe.isRefreshing = false
                    }
                })
            }
        }

    }


    override fun clickListener() {
        mSwipe.setOnRefreshListener {
            mListener.resetState()
            mOffset = 0
            getChatList(false)
        }
    }
}