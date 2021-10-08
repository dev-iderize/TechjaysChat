package com.techjays.chatlibrary.chat

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.techjays.chatlibrary.ChatLibrary
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.api.LibAppServices
import com.techjays.chatlibrary.base.LibBaseActivity
import com.techjays.chatlibrary.chatlist.LibChatListAdapter
import com.techjays.chatlibrary.model.LibChatList
import com.techjays.chatlibrary.model.LibChatMessages
import com.techjays.chatlibrary.model.LibChatSocketMessages
import com.techjays.chatlibrary.model.common.Option
import com.techjays.chatlibrary.util.*
import com.techjays.chatlibrary.viewmodel.LibChatViewModel
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.*
import kotlin.math.log


/**
 * Created by Srinath on 21/09/21.
 **/


class LibChatActivity : LibBaseActivity(), View.OnClickListener, ChatSocketListener.CallBack,
    LibChatAdapter.Callback {

    private lateinit var mRecyclerView: RecyclerView
    lateinit var mSelectedLibChatUser: LibChatList
    var mOffset = 0
    var mLimit = 6
    var isNextLink = false
    private lateinit var mListener: EndlessRecyclerViewScrollListener

    //private lateinit var mSwipe: SwipeRefreshLayout
    private lateinit var libAppBar: LinearLayout
    private lateinit var libImgBack: ImageView
    private lateinit var libSendButton: ImageView
    private lateinit var libChatEdit: EditText
    private lateinit var libTxtName: TextView
    private lateinit var libDeleteButton: ImageView
    private lateinit var mLibChatViewModel: LibChatViewModel
    var mData = ArrayList<LibChatMessages>()
    private lateinit var mAdapterLib: LibChatAdapter
    private lateinit var client: OkHttpClient
    private lateinit var ws: WebSocket
    private lateinit var listener: ChatSocketListener
    private lateinit var libProfileImage: CircleImageView

    var DELETEFORME: Int = 0
    var DELETEFORALL: Int = 1

    private var deletetype: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lib_activity_chat)
        if (intent != null) {
            if (intent.extras?.containsKey("chat_user")!!) {
                mSelectedLibChatUser = intent.extras?.get("chat_user") as LibChatList
            }
        }
        when (ChatLibrary.instance.mColor) {
            "#FF878E" -> {
                Utility.statusBarColor(window, this, R.color.status_pink)
            }
        }
        init()
        start()
    }

    private fun start() {
        val request: Request = Request.Builder().url(ChatLibrary.instance.socketUrl).build()
        listener = ChatSocketListener(this)
        ws = client.newWebSocket(request, listener)
        client.dispatcher().executorService().shutdown()
    }

    override fun onDestroy() {
        super.onDestroy()
        ws.cancel()
    }

    override fun init() {
        mLibChatViewModel = LibChatViewModel(this)
        client = OkHttpClient()
        mRecyclerView = findViewById(R.id.chatRecyclerView)
        // mSwipe = findViewById(R.id.chat_swipe_refresh)
        libAppBar = findViewById(R.id.l1)
        libImgBack = findViewById(R.id.libImgBack)
        libSendButton = findViewById(R.id.btnSendMessage)
        libChatEdit = findViewById(R.id.etMessage)
        libTxtName = findViewById(R.id.libTvUserName)
        libProfileImage = findViewById(R.id.libImgProfile)
        libDeleteButton = findViewById(R.id.delete_button)
        clickListener()
        initRecycler()
        initObserver()
        initView()
        getChatMessage(true)
    }

    private fun initView() {
        libTxtName.text = "${mSelectedLibChatUser.mCompanyName}${mSelectedLibChatUser.mFirstName}"
        Utility.loadUserImage(
            mSelectedLibChatUser.mProfilePic,
            libProfileImage,
            this
        )
        libAppBar.setBackgroundColor(
            Color.parseColor(
                ChatLibrary.instance.mColor
            )
        )
        val mBackground: Drawable = libSendButton.background
        try {
            mBackground.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                Utility.getColor(
                    this,
                    if (ChatLibrary.instance.mColor == "#FF878E") R.color.app_pink else R.color.app_blue
                ), BlendModeCompat.SRC_ATOP
            )
        } catch (e: Exception) {
            throw e
        }
    }

    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = layoutManager
        mAdapterLib = LibChatAdapter(this, mData, this)
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

    private fun initObserver() {
        if (!mLibChatViewModel.getChatObserver().hasActiveObservers()) {
            mLibChatViewModel.getChatObserver().observe(this, {
                AppDialogs.hideProgressDialog()
                when (it?.requestType) {

                    LibAppServices.API.delete_messages.hashCode() -> {
                        if (it.responseStatus!!) {
                            val iterator = mData.iterator()
                            while (iterator.hasNext()) {
                                val item = iterator.next()
                                if (item.isChecked) {
                                    iterator.remove()
                                }
                            }
                            mAdapterLib.notifyDataSetChanged()
                        } else AppDialogs.showSnackbar(mRecyclerView, it.responseMessage)
                    }
                }
            })
        }
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
        libDeleteButton.setOnClickListener(this)
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
            libDeleteButton -> {
                val id = ArrayList<String>()
                for (i in mData) {
                    if (i.isChecked)
                        id.add(i.mMessageId)
                }
                if (checkInternet()) {
                    val option = ArrayList<Option>()
                    option.add(
                        Option(
                            DELETEFORME,
                            getString(R.string.deleteforme)
                        )
                    )
                    option.add(
                        Option(
                            DELETEFORALL,
                            getString(R.string.deleteforall)

                        )
                    )
                    AppDialogs.initOptionDialog(this,
                        option,
                        object : DialogOptionAdapter.Callback {
                            override fun select(position: Int, option: Option) {
                                if (checkInternet()) {
                                    AppDialogs.hidecustomView()
                                    when (option.mId) {
                                        DELETEFORALL -> {

                                        }
                                        DELETEFORME -> {

                                            Log.e("idssss",TextUtils.join(",", id))
                                            if (id.isNotEmpty())
                                                mLibChatViewModel.deleteMessages(mSelectedLibChatUser.mToUserId.toInt(),false,TextUtils.join(",", id))
                                            else AppDialogs.showSnackbar(mRecyclerView, "Please select something!")
                                        }


                                    }
                                }
                            }
                        })
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
                    /*libChatEdit.text = "".toEditable()*/
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

    override fun showDeleteButton() {
        libDeleteButton.visibility =
            if (libDeleteButton.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    override fun messageDeleteforMe() {

    }

    override fun messageDeleteforAll() {
    }


}