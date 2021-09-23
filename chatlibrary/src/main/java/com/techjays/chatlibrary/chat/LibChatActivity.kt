package com.techjays.chatlibrary.chat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.Util.AppDialogs
import com.techjays.chatlibrary.Util.ChatSocketListener
import com.techjays.chatlibrary.Util.EndlessRecyclerViewScrollListener
import com.techjays.chatlibrary.base.LibBaseActivity
import com.techjays.chatlibrary.model.LibChatList
import com.techjays.chatlibrary.model.LibChatMessages
import com.techjays.chatlibrary.model.LibChatSocketMessages
import com.techjays.chatlibrary.view_model.LibChatViewModel
import okhttp3.Request
import okhttp3.WebSocket
import java.util.*
import okhttp3.OkHttpClient


/**
 * Created by Srinath on 21/09/21.
 **/


class LibChatActivity : LibBaseActivity(), View.OnClickListener, ChatSocketListener.CallBack {

    private lateinit var mRecyclerView: RecyclerView
    lateinit var mSelectedLibChatUser: LibChatList
    var mOffset = 0
    var mLimit = 6
    var isNextLink = false
    private lateinit var mListener: EndlessRecyclerViewScrollListener

    //private lateinit var mSwipe: SwipeRefreshLayout
    private lateinit var libImgBack: ImageView
    private lateinit var libSendButton: ImageView
    private lateinit var libChatEdit: EditText
    private lateinit var libTxtName: TextView
    private lateinit var mLibChatViewModel: LibChatViewModel
    var mData = ArrayList<LibChatMessages>()
    private lateinit var mAdapterLib: LibChatAdapter
    private lateinit var client: OkHttpClient
    private lateinit var ws: WebSocket
    private lateinit var listener: ChatSocketListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lib_activity_chat)
        if (intent != null) {
            if (intent.extras?.containsKey("chat_user")!!) {
                mSelectedLibChatUser = intent.extras?.get("chat_user") as LibChatList
            }
        }
        init()
        start()
    }

    private fun start() {
        val request: Request = Request.Builder().url("ws://3.19.93.161:8765").build()
        listener = ChatSocketListener(this)
        ws = client.newWebSocket(request, listener)
        client.dispatcher().executorService().shutdown()
    }

    override fun init() {
        mLibChatViewModel = LibChatViewModel(this)
        client = OkHttpClient()
        mRecyclerView = findViewById(R.id.chatRecyclerView)
        // mSwipe = findViewById(R.id.chat_swipe_refresh)
        libImgBack = findViewById(R.id.libImgBack)
        libSendButton = findViewById(R.id.btnSendMessage)
        libChatEdit = findViewById(R.id.etMessage)
        libTxtName = findViewById(R.id.libTvUserName)
        clickListener()
        initRecycler()
        initView()
        getChatMessage(true)
    }

    private fun initView() {
        libTxtName.text = "${mSelectedLibChatUser.mCompanyName}${mSelectedLibChatUser.mFirstName}"
    }


    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = layoutManager
        mAdapterLib = LibChatAdapter(this, mData)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        mRecyclerView.adapter = mAdapterLib

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
        //mSwipe.isRefreshing = !checkInternet()
        if (checkInternet()) {
            if (show)
                AppDialogs.showProgressDialog(this)
            mLibChatViewModel.getChatMessage(mOffset, mLimit, mSelectedLibChatUser.mToUserId)
            if (!mLibChatViewModel.getChatObserver().hasActiveObservers()) {
                mLibChatViewModel.getChatObserver().observe(this, {
                    AppDialogs.hideProgressDialog()
                    //mSwipe.isRefreshing = false
                    if (it?.responseStatus!!) {
                        isNextLink = (it as LibChatMessages).mNextLink
                        if (mOffset == 0)
                            mData.clear()
                        mData.addAll(it.mData)
                        mAdapterLib.notifyDataSetChanged()
                        /* if (mData.isNotEmpty())
                             Handler(Looper.myLooper()!!).postDelayed({
                                 mRecyclerView.smoothScrollToPosition(mData.size+1)
                             }, 100)*/
                    } else {
                        AppDialogs.customOkAction(this, it.responseMessage)
                        AppDialogs.hideProgressDialog()
                        //mSwipe.isRefreshing = false
                    }
                })
            }
        }

    }

    override fun clickListener() {
//        mSwipe.setOnRefreshListener {
//            mListener.resetState()
//            mOffset = 0
//            getChatMessage(false)
//        }
        libImgBack.setOnClickListener(this)
        libSendButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            libImgBack -> {
                onBackPressed()
            }
            libSendButton -> {
                if (libChatEdit.text.isEmpty()) {
                    libChatEdit.error = "Enter your message"
                    libChatEdit.requestFocus()
                } else {
                    listener.sendChat(libChatEdit.text.toString(), mSelectedLibChatUser.mToUserId)
                }
            }
        }

    }

    override fun onMessageReceive(receivedNewMessage: LibChatSocketMessages) {
        val isMySelf = receivedNewMessage.mData?.mSender == null
        runOnUiThread {
            val newMessage = LibChatMessages()
            newMessage.mIsSentByMyself = isMySelf
            when {
                isMySelf -> {
                    newMessage.mMessage = libChatEdit.text.toString()
                    newMessage.mTimeStamp = receivedNewMessage.mData!!.mTimeStamp
                    libChatEdit.text = "".toEditable()
                    mData.add(0, newMessage)
                    mRecyclerView.smoothScrollToPosition(0)
                    mAdapterLib.notifyDataSetChanged()
                }
                mSelectedLibChatUser.mToUserId == receivedNewMessage.mData?.mSender?.mUserId.toString() -> {
                    newMessage.mMessage = receivedNewMessage.mData!!.mMessage
                    newMessage.mTimeStamp = receivedNewMessage.mData!!.mTimeStamp
                    libChatEdit.text = "".toEditable()
                    mData.add(0, newMessage)
                    mRecyclerView.smoothScrollToPosition(0)
                    mAdapterLib.notifyDataSetChanged()
                }
                else -> {
                    /*
                        * Build notification on receiving broadcast from channel "ChatLibraryBuildNotification"
                        * */
                    val intent = Intent("ChatLibraryBuildNotification");
                    intent.putExtra("data", Gson().toJson(receivedNewMessage));
                    sendBroadcast(intent);
                }
            }

        }
    }
}