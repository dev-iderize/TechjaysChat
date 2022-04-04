package com.techjays.chatlibrary.chat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.Settings
import android.renderscript.ScriptGroup
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.techjays.chatlibrary.ChatLibrary
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.api.LibAppServices
import com.techjays.chatlibrary.base.LibBaseActivity
import com.techjays.chatlibrary.constants.Constant
import com.techjays.chatlibrary.constants.Constant.CHAT_TYPE_FILE
import com.techjays.chatlibrary.constants.Constant.CHAT_TYPE_MESSAGE
import com.techjays.chatlibrary.model.*
import com.techjays.chatlibrary.model.common.Option
import com.techjays.chatlibrary.preview.LibVideoPreviewActivity
import com.techjays.chatlibrary.util.*
import com.techjays.chatlibrary.viewmodel.LibChatViewModel
import com.techjays.inappcamera.InAppCameraActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.lib_activity_chat.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.*


/**
 * Created by Srinath on 21/09/21.
 **/


class LibChatActivity : LibBaseActivity(), View.OnClickListener, ChatSocketListener.CallBack,
    LibChatAdapter.Callback, PickiTCallbacks, LibVideoPreviewActivity.Callback {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPath:String

    /*lateinit var mSelectedLibChatUser: LibChatList*/
    private var mChatData = LibChatUserModel()
    lateinit var path: Uri
    var mOffset = 0
    var mLimit = 6
    var isNextLink = false
    private lateinit var mListener: EndlessRecyclerViewScrollListener

    //private lateinit var mSwipe: SwipeRefreshLayout
    private lateinit var libAppBar: LinearLayout
    private lateinit var libImgBack: ImageView
    private lateinit var libHeader:LinearLayout
    private lateinit var libSendButton: ImageView
    private lateinit var libChatEdit: EditText
    private lateinit var libTxtName: TextView
    private lateinit var libDeleteButton: ImageView
    private lateinit var libBtnImage: ImageView
    private lateinit var libBtnVideo: ImageView
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
    private lateinit var mLibChatSocketMessages: LibChatSocketMessages
    private lateinit var pickiT: PickiT
    var DELETEFORME: Int = 0
    var DELETEFORALL: Int = 1
    var deleteforAll = false
    var isResume = false

    val id = ArrayList<String>()
    var mResumePath = ""
    private var mPermission =
        arrayOf(
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
        )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lib_activity_chat)
        if (intent != null) {
            /* if (intent.extras?.containsKey("chat_user")!!) {
                 mChatData = intent.extras?.get("chat_user") as LibChatUserModel
             }*/
            val data = intent

            mChatData = Gson().fromJson(
                data.getStringExtra("chat_user").toString(),
                LibChatUserModel::class.java
            )
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
        pickiT = PickiT(this, this, this)
        Utility.setBackgroundDrawableResource(this.window, R.drawable.img_bg)
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
        libBtnImage = findViewById(R.id.btnSendImage)
        libBtnVideo = findViewById(R.id.btn_send_video)
        libHeader = findViewById(R.id.header_part)

        libBtnVideo.setOnClickListener {
            val intent = Intent(this, InAppCameraActivity::class.java)
            intent.putExtra("video_limit", false)
            startActivityForResult(intent, 5)
        }

        libHeader.setOnClickListener {
            setResult(1002)
            finish()
        }

        if (!mChatData.mIsTypeMessage)
            r1.visibility = View.GONE
        else
            r1.visibility = View.VISIBLE


        clickListener()
        initRecycler()
        initObserver()
        initFileObserver()
        initView()
        PermissionChecker().askAllPermissions(this, mPermission)
        getChatMessage(true)
    }


    @SuppressLint("SetTextI18n")
    private fun initView() {
        libTxtName.text = mChatData.mReceiverFullName
        lib_username_vs.text = "${mChatData.mSenderFullName} vs ${mChatData.mReceiverFullName}"
        lib_event_name.text = mChatData.mEventName
        lib_total_bid.text = "Total bid: ${mChatData.mBidAmount}"
        Utility.loadUserImage(
            mChatData.mReceiverProfilePicUrl,
            libProfileImage,
            this
        )
        if (!mChatData.mIsPdf) {
            btnSendFile.visibility = View.GONE
        }
        /*libAppBar.setBackgroundColor(
            Color.parseColor(
                ChatLibrary.instance.mColor
            )
        )*/
        /*val mBackground: Drawable = libSendButton.background
        try {
            mBackground.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                Utility.getColor(
                    this,
                    if (ChatLibrary.instance.mColor == "#FF878E") R.color.app_pink else R.color.app_blue
                ), BlendModeCompat.SRC_ATOP
            )
        } catch (e: Exception) {
            throw e
        }*/
    }

    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = layoutManager
        mAdapterLib = LibChatAdapter(this, mData, mChatData, this)
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
            mLibChatViewModel.getChatObserver().observe(this) {
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
            }
        }
    }

    private fun initFileObserver() {
        if (!mLibChatViewModel.getChatFileObserver().hasActiveObservers()) {
            mLibChatViewModel.getChatFileObserver().observe(this) {
                AppDialogs.hideProgressDialog()
                when (it?.requestType) {
                    LibAppServices.API.upload_file.hashCode() -> {
                        if (it.responseStatus!!) {
                            mLibChatSocketMessages = (it as LibChatSocketMessages).mData!!
                            mLibChatSocketMessages.mMessageType = CHAT_TYPE_FILE
                            listener.sendChatFile(
                                mLibChatSocketMessages.mFile,
                                mChatData.mReceiverUserId,
                                CHAT_TYPE_FILE, mLibChatSocketMessages, mChatData.mItemId
                            )

                        } else AppDialogs.showSnackbar(mRecyclerView, it.responseMessage)
                    }
                }
            }
        }
    }

    private fun getChatMessage(show: Boolean) {
        //mSwipe.isRefreshing = !checkInternet()
        if (checkInternet()) {
            if (show)
                AppDialogs.showProgressDialog(this)
            mLibChatViewModel.getChatMessage(
                mOffset,
                mLimit,
                mChatData.mReceiverUserId,
                mChatData.mItemId
            )
            if (!mLibChatViewModel.getChatObserver().hasActiveObservers()) {
                mLibChatViewModel.getChatObserver().observe(this) {
                    AppDialogs.hideProgressDialog()
                    //mSwipe.isRefreshing = false
                    if (it?.requestType == LibAppServices.API.get_chat_message.hashCode() && it.responseStatus!!) {
                        isNextLink = (it as LibChatMessages).mNextLink
                        if (mOffset == 0)
                            mData.clear()
                        mData.addAll(it.mData)
                        mAdapterLib.notifyDataSetChanged()
                        /* if (mData.isNotEmpty())
                             Handler(Looper.myLooper()!!).postDelayed({
                                 mRecyclerView.smoothScrollToPosition(mData.size+1)
                             }, 100)*/
                    } else if (!mLibChatViewModel.getChatImageObserver()
                            .hasActiveObservers()
                    ) else {
                        AppDialogs.customOkAction(this, it!!.responseMessage)
                        AppDialogs.hideProgressDialog()
                        //mSwipe.isRefreshing = false
                    }
                }
            }
        }

    }

    fun sendClickable(click: Boolean) {
        libSendButton.isClickable = click
    }

    private fun deleteInvisible() {
        Constant.COUNTER_DELETE_CHECKBOX = 0
        showDeleteButton()
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
        libBtnImage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            libImgBack -> {
                onBackPressed()
            }
            libHeader ->{
                setResult(1002)
                finish()
            }
            libBtnImage -> {
                /*  val options: Options = Options.init()
                      .setRequestCode(100) //Request code for activity results
                      .setCount(1) //Number of images to restict selection count
                      .setFrontfacing(false) //Front Facing camera on start
                      .setSpanCount(4) //Span count for gallery min 1 & max 5
                      .setMode(Options.Mode.Picture) //Option to select only pictures or videos or both
                      .setVideoDurationLimitinSeconds(30) //Duration for video recording
                      .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT) //Orientaion
                      .setPath("/vidrivals/images") //Custom Path For media Storage


                  Pix.start(this@LibChatActivity, options)*/

                ImagePicker.with(this)
                    .crop()                    //Crop image(Optional), Check Customization for more option
                    .compress(1024)            //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080,
                        1080
                    )    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start()
            }
            libBtnVideo -> {
                val intent = Intent(this, InAppCameraActivity::class.java)
                intent.putExtra("video_limit", false)
                startActivityForResult(intent, 5)
            }
            libSendButton -> {
                if (Utility.isOpenRecently())
                    return

                var str = libChatEdit.text.toString()

                if (libChatEdit.text.trim().isEmpty() || str=="") {
                    libChatEdit.error = "Enter your message"
                    libChatEdit.requestFocus()
                } else {
                    listener.sendChat(
                        libChatEdit.text.toString(),
                        mChatData.mReceiverUserId,
                        CHAT_TYPE_MESSAGE, mChatData.mItemId
                    )
                }

                val iterator = mData.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    if (item.showCheckBox) {
                        item.showCheckBox = !item.showCheckBox
                    }
                }
                deleteInvisible()
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

                if (PermissionChecker().checkAllPermission(this, mPermission)) {

                    Utility.isOpenRecently()
                    // uploadFile()
                    selectFile()
                }
            }
        }
    }

    private fun uploadFile() {

        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data =
                    Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, 2296)
            } catch (e: java.lang.Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 2296)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                mPermission,
                Constant.REQUEST_CODE_PERMISSION
            )
        }
    }


    private fun selectFile() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "application/pdf"
        startActivityForResult(
            Intent.createChooser(intent, "Choose a file"),
            234
        )
    }

    private fun deleteChatMessages(deleteforme: Boolean) {
        for (i in mData) {
            if (i.isChecked)
                id.add(i.mMessageId)
        }
        if (id.isNotEmpty()) {
            mLibChatViewModel.deleteMessages(
                mChatData.mReceiverUserId.toInt(),
                deleteforme,
                TextUtils.join(",", id), mChatData.mItemId
            )
            id.clear()
        } else AppDialogs.showSnackbar(
            mRecyclerView,
            "Please select something!"
        )
    }

    override fun onMessageReceive(receivedNewMessage: LibChatSocketMessages) {
        val isMySelf = receivedNewMessage.mData?.mSender == null
        runOnUiThread {
            val newMessage = LibChatMessages()
            newMessage.mIsSentByMyself = isMySelf
            newMessage.mMessageId = receivedNewMessage.mData!!.mMessageId
            newMessage.mMessage = receivedNewMessage.mData!!.mMessage
            newMessage.mMessageType = receivedNewMessage.mData!!.mMessageType
            newMessage.mFileType = receivedNewMessage.mData!!.mFileType
            newMessage.mFileThumbNail = receivedNewMessage.mData!!.mFileThumbNail
            newMessage.mTimeStamp = receivedNewMessage.mData!!.mTimeStamp
            newMessage.mDuelId = receivedNewMessage.mData!!.mDuelId
            Utility.log(receivedNewMessage.mMessageId + " ithu")
            when {
                isMySelf -> {
                    newMessage.mMessage = receivedNewMessage.mData!!.mMessage
                    newMessage.mTimeStamp = receivedNewMessage.mData!!.mTimeStamp
                    libChatEdit.text = "".toEditable()
                    mData.add(0, newMessage)
                    mRecyclerView.smoothScrollToPosition(0)
                    mAdapterLib.notifyDataSetChanged()
                }
                mChatData.mReceiverUserId == receivedNewMessage.mData?.mSender?.mUserId.toString() -> {
                    AppDialogs.showToastshort(this,"here")
                    newMessage.mMessage = receivedNewMessage.mData!!.mMessage
                    newMessage.mTimeStamp = receivedNewMessage.mData!!.mTimeStamp
                    /*libChatEdit.text = "".toEditable()*/
                    mData.add(0, newMessage)
                    mRecyclerView.smoothScrollToPosition(0)
                    mAdapterLib.notifyDataSetChanged()
                }
                mChatData.mItemId != receivedNewMessage.mData!!.mDuelId!!.toString() ->{
                    val intent = Intent("ChatLibraryBuildNotification")
                    intent.putExtra("data", Gson().toJson(receivedNewMessage))
                    sendBroadcast(intent)
                }
                else -> {
                    /*
                        * Build notification on receiving broadcast from channel "ChatLibraryBuildNotification"
                        * */
                    val intent = Intent("ChatLibraryBuildNotification")
                    intent.putExtra("data", Gson().toJson(receivedNewMessage))
                    sendBroadcast(intent)
                }
            }

        }
    }

    override fun showDeleteButton() {
        Log.e("counter", Constant.COUNTER_DELETE_CHECKBOX.toString())
        libDeleteButton.visibility =
            if (Constant.COUNTER_DELETE_CHECKBOX > 0) View.VISIBLE else View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == 100) {
                val returnValue: ArrayList<String> =
                    data.getStringArrayListExtra(Pix.IMAGE_RESULTS)!!
                Log.d("image uri", returnValue[0])
                if (checkInternet()) {
                    AppDialogs.showProgressDialog(this)
                    mLibChatViewModel.uploadImageVideo(returnValue[0], "image")
                }

            } else if (requestCode == 234) {
                isResume = true
                pickiT?.getPath(data?.data, 31)

            } else if (requestCode == 5) {
                if (resultCode === RESULT_OK) { // Activity.RESULT_OK
                    Log.d("check", data!!.getStringExtra("path")!!)
                    showPreview(data!!.getStringExtra("path")!!)
                    mPath = data!!.getStringExtra("path")!!
                }
            }
             else {
                if (data?.data != null) {
                    val uri: Uri = data?.data!!
                    isResume = false
                    pickiT?.getPath(data?.data, 31)
                    Utility.log(uri.toString())
                }
            }

        }


    }

    private fun showPreview(path: String) {
        LibVideoPreviewActivity.newInstance(this)
        val i = Intent(this, LibVideoPreviewActivity::class.java)
        i.putExtra("url_data", path)
        i.putExtra("preview",true)
        startActivityForResult(i,6000)
    }


    private fun initFileUpload() {
        if (checkInternet()) {
            AppDialogs.showProgressDialog(this)
            val chatmessages = LibChatMessages()
            chatmessages.mToUserId = mChatData.mReceiverUserId
            chatmessages.mFile = mResumePath
            mLibChatViewModel.uploadFile(chatmessages)
        }
    }

    override fun PickiTonUriReturned() {
    }

    override fun PickiTonStartListener() {
    }

    override fun PickiTonProgressUpdate(progress: Int) {
    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        if (path != null) {
            if (isResume) {
                mResumePath = path
                initFileUpload()
            } else {
                if (checkInternet()) {
                    AppDialogs.showProgressDialog(this)
                    mLibChatViewModel.uploadImageVideo(path, "image")
                }
            }

            /*Utility.log(path)*/
        }
    }

    override fun onBackPressed() {
        setResult(10050)
        finish()
        super.onBackPressed()
    }

    override fun videoPreviewCallback(mUrl: String) {
        if (checkInternet()) {
            AppDialogs.showProgressDialog(this)
            mLibChatViewModel.uploadImageVideo(mUrl, "video")
        }
    }
}