package com.techjays.chatlibrary.chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.Util.AppDialogs
import com.techjays.chatlibrary.Util.EndlessRecyclerViewScrollListener
import com.techjays.chatlibrary.base.BaseActivity
import com.techjays.chatlibrary.model.ChatList
import com.techjays.chatlibrary.model.ChatMessages
import com.techjays.chatlibrary.view_model.ChatViewModel
import java.util.*

/**
 * Created by Srinath on 21/09/21.
 **/

class ChatActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mRecyclerView: RecyclerView
    lateinit var mSelectedChatUser: ChatList
    var mOffset = 0
    var mLimit = 6
    var isNextLink = false
    private lateinit var mListener: EndlessRecyclerViewScrollListener
    private lateinit var mSwipe: SwipeRefreshLayout
    private lateinit var imgBack: ImageView
    private lateinit var txtName:TextView
    private lateinit var mChatViewModel: ChatViewModel
    var mData = ArrayList<ChatMessages>()
    private lateinit var mAdapter: ChatAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        if (intent != null) {
            if (intent.extras?.containsKey("chat_user")!!) {
                mSelectedChatUser = intent.extras?.get("chat_user") as ChatList
            }
        }
        init()
    }

    override fun init() {
        mChatViewModel = ChatViewModel(this)
        mRecyclerView = findViewById(R.id.chatRecyclerView)
        mSwipe = findViewById(R.id.chat_swipe)
        imgBack = findViewById(R.id.imgBack)
        txtName = findViewById(R.id.tvUserName)
        clickListener()
        initRecycler()
        initView()
        getChatMessage(true)
    }

    private fun initView() {
        txtName.text = "${mSelectedChatUser.mCompanyName}${mSelectedChatUser.mFirstName}"
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
                        getChatMessage(false)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace(

                    )
                }
            }
        }
        mRecyclerView.addOnScrollListener(mListener)

    }

    private fun getChatMessage(show: Boolean) {
        mSwipe.isRefreshing = !checkInternet()
        if (checkInternet()) {
            if (show)
                AppDialogs.showProgressDialog(this)
            mChatViewModel.getChatMessage(mOffset, mLimit, mSelectedChatUser.mToUserId)
            if (!mChatViewModel.getChatObserver().hasActiveObservers()) {
                mChatViewModel.getChatObserver().observe(this, {
                    AppDialogs.hideProgressDialog()
                    mSwipe.isRefreshing = false
                    if (it.responseStatus!!) {
                        isNextLink = (it as ChatMessages).mNextLink
                        if (mOffset == 0)
                            mData.clear()
                        mData.addAll(it.mData)
                        mAdapter.notifyDataSetChanged()
                        if (mData.isNotEmpty())
                            Handler(Looper.myLooper()!!).postDelayed({
                                mRecyclerView.smoothScrollToPosition(mData.size - 1)
                            }, 100)
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
            getChatMessage(false)
        }
        imgBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            imgBack -> {
                onBackPressed()
            }

        }

    }
}