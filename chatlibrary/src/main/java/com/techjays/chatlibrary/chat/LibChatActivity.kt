package com.techjays.chatlibrary.chat

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
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
import com.techjays.chatlibrary.model.LibChatList
import com.techjays.chatlibrary.model.LibChatMessages
import com.techjays.chatlibrary.model.LibChatSocketMessages
import com.techjays.chatlibrary.model.common.Option
import com.techjays.chatlibrary.util.*
import com.techjays.chatlibrary.viewmodel.LibChatViewModel
import de.hdodenhof.circleimageview.CircleImageView
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.utils.ContentUriUtils
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.*


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
    private lateinit var mBtnFile: ImageView
    var WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    var READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

    var DELETEFORME: Int = 0
    var DELETEFORALL: Int = 1
    var deleteforAll = LibChatMessages().deleteTypeForAll

    val id = ArrayList<String>()
    var mResumePath = ""
    private var mPermission =
        arrayOf(
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE,
        )


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
        mBtnFile = findViewById(R.id.btnSendFile)
        clickListener()
        initRecycler()
        initObserver()
        initFileObserver()
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
                            Toast.makeText(
                                this,
                                getString(R.string.delete_string),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else AppDialogs.showSnackbar(mRecyclerView, it.responseMessage)
                    }
                }
            })
        }
    }

    private fun initFileObserver() {
        if (!mLibChatViewModel.getChatFileObserver().hasActiveObservers()) {
            mLibChatViewModel.getChatFileObserver().observe(this, {
                AppDialogs.hideProgressDialog()
                when (it?.requestType) {
                    LibAppServices.API.upload_file.hashCode() -> {
                        if (it.responseStatus!!) {

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
                        LibChatMessages().isChecked = false
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
        mBtnFile.setOnClickListener(this)
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
                deleteforAll = true

                for (i in mData) {
                    if (i.isChecked && !i.mIsSentByMyself) {
                        deleteforAll = false
                        break
                    }
                }
                if (checkInternet()) {
                    val option = ArrayList<Option>()
                    option.add(
                        Option(
                            DELETEFORME,
                            getString(R.string.deleteforme),
                            Utility.getDrawable(this, R.drawable.lib_ic_baseline_delete_24)
                        )
                    )
                    if (deleteforAll)
                        option.add(
                            Option(
                                DELETEFORALL,
                                getString(R.string.deleteforall),
                                Utility.getDrawable(this, R.drawable.lib_ic_baseline_delete_24)

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
                                            deleteChatMessages(false)

                                        }
                                        DELETEFORME -> {
                                            deleteChatMessages(true)
                                        }
                                    }
                                }
                            }
                        })
                }
            }
            mBtnFile -> {
                Utility.isOpenRecently()
                uploadResume()
            }
        }
    }

    private fun uploadResume() {
        if (PermissionChecker().checkAllPermission(this, mPermission))
            FilePickerBuilder.instance
                .setMaxCount(1)
                .setActivityTheme(R.style.PickerTheme).enableDocSupport(false)
                .addFileSupport("Select your pdf", arrayOf("pdf", "PDF"))
                .pickFile(this)
    }

    private fun deleteChatMessages(deleteforme: Boolean) {

        for (i in mData) {
            if (i.isChecked)
                id.add(i.mMessageId)
        }
        if (id.isNotEmpty())
            mLibChatViewModel.deleteMessages(
                mSelectedLibChatUser.mToUserId.toInt(),
                deleteforme,
                TextUtils.join(",", id)
            )
        else AppDialogs.showSnackbar(
            mRecyclerView,
            "Please select something!"
        )
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

    override fun showDeleteButton(count: Int) {
        libDeleteButton.visibility = if (count > 0) View.VISIBLE else View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
            if (resultCode == RESULT_OK && data != null) {
                val uri =
                    data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_DOCS)!![0]
                mResumePath = ContentUriUtils.getFilePath(this, uri)!!
                initFileUpload()
            }
        }
    }

    private fun initFileUpload() {
        if (checkInternet()) {
            AppDialogs.showProgressDialog(this)
            val chatmessages = LibChatMessages()
            chatmessages.mToUserId = mSelectedLibChatUser.mToUserId
            chatmessages.mFile = mResumePath
            mLibChatViewModel.uploadFile(chatmessages)
        }
    }
}