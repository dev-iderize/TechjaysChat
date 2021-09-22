package com.techjays.chatlibrary.chat

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
import com.techjays.chatlibrary.base.BaseActivity
import com.techjays.chatlibrary.model.ChatList
import com.techjays.chatlibrary.model.ChatMessages
import com.techjays.chatlibrary.model.ChatSocketMessages
import com.techjays.chatlibrary.view_model.ChatViewModel
import okhttp3.Request
import okhttp3.WebSocket
import java.util.*
import okhttp3.OkHttpClient


/**
 * Created by Srinath on 21/09/21.
 **/


class LibChatActivity : BaseActivity(), View.OnClickListener, ChatSocketListener.CallBack {

    private lateinit var mRecyclerView: RecyclerView
    lateinit var mSelectedChatUser: ChatList
    var mOffset = 0
    var mLimit = 6
    var isNextLink = false
    private lateinit var mListener: EndlessRecyclerViewScrollListener

    //private lateinit var mSwipe: SwipeRefreshLayout
    private lateinit var imgBack: ImageView
    private lateinit var sendButton: ImageView
    private lateinit var chatEdit: EditText
    private lateinit var txtName: TextView
    private lateinit var mChatViewModel: ChatViewModel
    var mData = ArrayList<ChatMessages>()
    private lateinit var mAdapterLib: LibChatAdapter
    private lateinit var client: OkHttpClient
    private lateinit var ws: WebSocket
    private lateinit var listener: ChatSocketListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        if (intent != null) {
            if (intent.extras?.containsKey("chat_user")!!) {
                mSelectedChatUser = intent.extras?.get("chat_user") as ChatList
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
        mChatViewModel = ChatViewModel(this)
        client = OkHttpClient()
        mRecyclerView = findViewById(R.id.chatRecyclerView)
        // mSwipe = findViewById(R.id.chat_swipe_refresh)
        imgBack = findViewById(R.id.imgBack)
        sendButton = findViewById(R.id.btnSendMessage)
        chatEdit = findViewById(R.id.etMessage)
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
            mChatViewModel.getChatMessage(mOffset, mLimit, mSelectedChatUser.mToUserId)
            if (!mChatViewModel.getChatObserver().hasActiveObservers()) {
                mChatViewModel.getChatObserver().observe(this, {
                    AppDialogs.hideProgressDialog()
                    //mSwipe.isRefreshing = false
                    if (it?.responseStatus!!) {
                        isNextLink = (it as ChatMessages).mNextLink
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
        imgBack.setOnClickListener(this)
        sendButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            imgBack -> {
                onBackPressed()
            }
            sendButton -> {
                if (chatEdit.text.isEmpty()) {
                    chatEdit.error = "Enter your message"
                    chatEdit.requestFocus()
                } else {
                    listener.sendChat(chatEdit.text.toString(), mSelectedChatUser.mToUserId)
                    chatEdit.text = "".toEditable()
                }
            }
        }

    }

    override fun onMessageReceive(receivedNewMessage: ChatSocketMessages) {
        val newMessage = ChatMessages()
        newMessage.mIsSentByMyself = false
        newMessage.mMessage = receivedNewMessage.mMessage
        newMessage.mTimeStamp = receivedNewMessage.mTimeStamp
        mData.add(mData.size - 1, newMessage)
        mAdapterLib.notifyDataSetChanged()
    }
}