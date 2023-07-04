package com.techjays.chatlibrary.chat

import AudioRecorder
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devlomi.record_view.OnRecordListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.stfalcon.imageviewer.StfalconImageViewer
import com.techjays.chatlibrary.ChatLibrary
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.api.LibAppServices
import com.techjays.chatlibrary.api.Response
import com.techjays.chatlibrary.api.ResponseListener
import com.techjays.chatlibrary.databinding.ActivityChatBinding
import com.techjays.chatlibrary.databinding.BottomSheetLayoutBinding
import com.techjays.chatlibrary.interfaces.FileUploadProgress
import com.techjays.chatlibrary.model.Chat
import com.techjays.chatlibrary.model.LibChatSocketMessages
import com.techjays.chatlibrary.model.MyMessage
import com.techjays.chatlibrary.model.OthersMessage
import com.techjays.chatlibrary.util.AppDialogs
import com.techjays.chatlibrary.util.ChatSocketListener
import com.techjays.chatlibrary.util.EndlessRecyclerViewScrollListener
import com.techjays.chatlibrary.util.Helper
import com.techjays.chatlibrary.util.PermissionChecker
import com.techjays.chatlibrary.util.Utility
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class LibChatActivity : AppCompatActivity(), TextWatcher, ResponseListener,
    AudioRecorder.AudioRecorderCallBack, ChatSocketListener.SocketCallback, FileUploadProgress,
    ChatAdapter.ChatCallback {
    private val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST: Int = 10002
    private val CAMERA_PERMISSION_CODE: Int = 10004
    private val VIDEO_CAPTURE_REQUEST_CODE = 4001
    lateinit var binding: ActivityChatBinding
    var scrolledToTop = false
    private lateinit var audioRecorder: AudioRecorder
    var mediaPlayer: MediaPlayer? = null
    var mDialogAction = ""
    var mAudioPath: String = ""
    private var groupId = -1
    var mOffset = 0
    var mLimit = 10
    var isNextLink = false
    private lateinit var mListener: EndlessRecyclerViewScrollListener
    private lateinit var listener: ChatSocketListener
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private lateinit var videoPickerLauncher: ActivityResultLauncher<String>
    private lateinit var audioPickerLauncher: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var videoCaptureLauncher: ActivityResultLauncher<Intent>

    private var currentPhotoPath: String? = null

    private var imageUri: Uri? = null
    private var ws: WebSocket? = null
    private lateinit var client: OkHttpClient
    var groupName = ""
    var myId = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this,
                R.layout.activity_chat
            ) as ActivityChatBinding
        Utility.statusBarColor(window, applicationContext, R.color.primary_color_light)
        audioRecorder = AudioRecorder(this, this, packageName + "fileProvider")
        myId = ChatLibrary.instance.mUserId

        groupId = intent.getIntExtra("groupId", -1)
        val aCreatorId = intent.getIntExtra("creatorId", -1)
        Log.e("creatorId_______>", groupId.toString())
        groupName = if (aCreatorId == myId)
            "My Circle"
        else
            intent.getStringExtra("groupName")!!
        client = OkHttpClient()
        val groupProfilePic = intent.getStringExtra("groupProfilePic")
        binding.groupName = groupName
        listener = ChatSocketListener(this, ws, this)
        binding.groupProfilePic = groupProfilePic
        webSocketStart()
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri != null) {
                    fileUpload(uri)
                }
            }

        videoPickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri != null) {
                    fileUpload(uri)
                }
            }

        audioPickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri != null) {
                    fileUpload(uri)
                }
            }
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    imageUri?.let { uri ->
                        fileUpload(uri)
                    }
                }
            }
        videoCaptureLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val videoUri: Uri? = result.data?.data
                    if (videoUri != null)
                        fileUpload(videoUri)
                } else {
                    //  AppDialogs.showToastDialog(this, "Video capture failed")
                }
            }


    }


    private fun fileUpload(uri: Uri) {
        if (Utility.checkInternet(this))
            LibAppServices.fileUpload(this, uri, this, this)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = createImageFile(this)
        photoFile?.let { file ->
            val photoURI: Uri = getFileUri(this, file)
            imageUri = photoURI
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            cameraLauncher.launch(takePictureIntent)
        }
    }

    private fun createImageFile(context: Context): File? {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )

        currentPhotoPath = imageFile.absolutePath

        return imageFile
    }

    private fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            context.packageName + "fileProvider",
            file
        )
    }


    fun getChatMessage() {
        if (Utility.checkInternet(this))
            LibAppServices.getChatsFromGroup(this, mOffset, mLimit, groupId, this)
    }

    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.layoutManager = layoutManager
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        mListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                try {
                    if (isNextLink) {
                        mOffset += mLimit
                        getChatMessage()
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
        binding.chatRecyclerView.addOnScrollListener(mListener)
    }

    override fun onResume() {
        super.onResume()
        init()
        registerReceiver(
            chatWebSocketBroadcast, IntentFilter("chat_web_socket_message")
        )
    }

    private fun init() {
        binding.activity = this
        binding.isActive = true
        mOffset = 0
        getChatMessage()
        initRecycler()
        binding.recordButton.setRecordView(binding.recordView)
        binding.chatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && binding.fab.isShown) {
                    binding.fab.hide()
                } else if (dy < 0 && !binding.fab.isShown) {
                    scrolledToTop = true
                    binding.fab.hide()
                }
            }
        })
        binding.fab.setOnClickListener {
            binding.chatRecyclerView.scrollToPosition(0)
            binding.fab.hide()
        }
        binding.isMicPermissionAvailable = PermissionChecker().checkPermission(
            applicationContext,
            android.Manifest.permission.RECORD_AUDIO
        )
        binding.recordView.visibility = View.GONE
        binding.etMessage.visibility = View.VISIBLE
        binding.etMessage.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                if (v.text.toString().trim().isNotEmpty()) {
                    if (ws != null)
                        listener.sendChat(v.text.toString(), groupId, groupName, ws!!)
                    v.text = ""
                    v.clearFocus()
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        binding.recordView.setOnRecordListener(object : OnRecordListener {
            override fun onStart() {
                showHideLayouts(true)
                audioRecorder.startRecording()

            }

            override fun onCancel() {
                showHideLayouts(false)
                audioRecorder.stopRecording()

            }

            override fun onFinish(recordTime: Long, limitReached: Boolean) {
                showHideLayouts(false)
                audioRecorder.stopRecording()


            }

            override fun onLessThanSecond() {
                showHideLayouts(false)
                if (audioRecorder != null)
                    audioRecorder.stopRecording()
            }


            override fun onLock() {
            }

        })

    }


    fun openMicDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Permission denied")
        builder.setMessage("Please enable microphone permission to record your audio")
        builder.setPositiveButton("Go to settings") { _, _ ->
            Helper.navigateAppSetting(this)
        }
        builder.setNegativeButton("Cancel", null)
        builder.create().show()
    }

    private fun showHideLayouts(isVoice: Boolean) {
        binding.etMessage.visibility = if (isVoice) View.GONE else View.VISIBLE
        binding.recordView.visibility = if (isVoice) View.VISIBLE else View.GONE
    }

    fun showBottomSheet() {
        val bottomSheetView =
            BottomSheetLayoutBinding.inflate(LayoutInflater.from(applicationContext))
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView.root)

        bottomSheetView.galleryButton.setOnClickListener {
            bottomSheetDialog.dismiss()
            mDialogAction = "IMAGE"
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_PERMISSION_REQUEST
                )
            } else {
                openImagePicker()
            }
        }

        bottomSheetView.cameraButton.setOnClickListener {
            bottomSheetDialog.dismiss()
            mDialogAction = "CAMERA"
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_PERMISSION_REQUEST
                )
            } else {
                dispatchTakePictureIntent()
            }
        }
        bottomSheetView.videoButton.setOnClickListener {
            bottomSheetDialog.dismiss()
            mDialogAction = "VIDEO"
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_PERMISSION_REQUEST
                )
            } else {
                openVideoPicker()
            }
        }
        bottomSheetView.musicButton.setOnClickListener {
            bottomSheetDialog.dismiss()
            mDialogAction = "AUDIO"
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_PERMISSION_REQUEST
                )
            } else {
                openAudioPicker()
            }
        }
        bottomSheetView.cameraVideoButon.setOnClickListener {
            bottomSheetDialog.dismiss()
            mDialogAction = "CAPTURE_VIDEO"
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_PERMISSION_REQUEST
                )
            } else {
                dispatchTakeVideoIntent(this)
            }
        }

        bottomSheetDialog.show()
    }

    private fun webSocketStart() {
        val request: Request =
            Request.Builder().url(ChatLibrary.instance.socketUrl).build()
        ws = client.newWebSocket(request, listener)
        listener.initialize(ws!!)
    }


    private fun dispatchTakeVideoIntent(context: Context) {
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (takeVideoIntent.resolveActivity(packageManager) != null) {
            val permission = Manifest.permission.CAMERA
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    VIDEO_CAPTURE_REQUEST_CODE
                )
            } else {
                val videoFile: File? = createVideoFile()
                if (videoFile != null) {
                    val videoUri: Uri = FileProvider.getUriForFile(
                        this,
                        context.packageName + "fileProvider",
                        videoFile!!
                    )
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
                    videoCaptureLauncher.launch(
                        Intent.createChooser(
                            takeVideoIntent,
                            "Capture Video"
                        )
                    )
                } else {

                }
            }
        }
    }


    private fun createVideoFile(): File? {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val videoFileName = "VIDEO_$timeStamp"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_MOVIES)

        return try {
            File.createTempFile(
                videoFileName,
                ".mp4",
                storageDir
            )
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        if (s != null) {
            if (s.trim().isNotEmpty()) {
                showHideLayouts(false)

            }
        }

    }


    fun scrollToBottom() {
        val adapter = binding.chatRecyclerView.adapter as ChatAdapter
        if (adapter != null) {
            val itemCount: Int = adapter.itemCount
            if (itemCount > 0) {
                binding.chatRecyclerView.scrollToPosition(0)
            }
        }
    }

    override fun onPause() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onPause()
    }

    override fun onBackPressed() {
        mediaPlayer?.release()
        mediaPlayer = null
        ws?.cancel()
        super.onBackPressed()

    }

    override fun onDestroy() {
        super.onDestroy()
        ws?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
        unregisterReceiver(chatWebSocketBroadcast)
    }

    @Suppress("NAME_SHADOWING")
    fun showImageViewer(imageUrl: String) {
        val factory = LayoutInflater.from(this)
        val images = listOf(imageUrl)
        StfalconImageViewer.Builder(this, images) { view, imageUrl ->
            Glide.with(this).load(imageUrl).into(view)
        }.withHiddenStatusBar(false).show()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResponse(r: Response?) {
        if (r != null) {
            when (r.requestType) {
                LibAppServices.API.get_chats.hashCode() -> {
                    if (r.responseStatus!!) {
                        val ct = r as Chat
                        isNextLink = r.next_link
                        if (mOffset == 0) {
                            binding.chatdata = ct
                            binding.chatRecyclerView.adapter = ChatAdapter(this, r.mData, this)


                        } else {
                            binding.chatdata!!.mData.addAll(ct.mData)
                        }
                        binding.chatRecyclerView.adapter!!.notifyDataSetChanged()
                        if (mOffset == 0)
                            scrollToBottom()

                    }
                }

                LibAppServices.API.upload_file.hashCode() -> {
                    if (r.responseStatus!!) {
                        r as LibChatSocketMessages
                        listener.sendFileParams(r.mMessage, groupId, r, groupName)

                    } else
                        AppDialogs.showToastshort(this, r.responseMessage)
                }

            }

        }
    }

    fun MyMessage.toChatModel(isSentMyself: Boolean?): Chat {
        val chat = Chat()
        chat.mData = arrayListOf(
            Chat.ChatData().apply {
                mMessageId = this@toChatModel.data.messageId
                mMessageType = this@toChatModel.data.messageType
                mFileType = this@toChatModel.data.fileType
                mMessage = this@toChatModel.data.message
                mTime = this@toChatModel.data.timestamp
                mMediumImage =
                    if (mMessageType == "file") this@toChatModel.data.thumbnailImage else ""
                mThumbnailImage =
                    if (mMessageType == "file") this@toChatModel.data.thumbnailImage else ""
                mProfilePic = ""
                mFirstName = ""
                mLastName = ""
                mPhoneNumber = data.mPhoneNumber
                mName = "$mFirstName $mLastName"
                mUserId = -1
                isSentByMyself = isSentMyself ?: false
                mLastSentMsgTimeStamp = ""
                mIsRead = false
            }
        )
        return chat
    }

    fun OthersMessage.toChat(isSentMyself: Boolean?): Chat {
        val chat = Chat()
        val chatData = Chat.ChatData()

        chatData.mMessageId = data.messageId
        chatData.mMessageType = if (data.message.contains("chat")) "message" else data.messageType
        chatData.mFileType = data.fileType
        chatData.mMessage = data.message
        chatData.mTime = data.timestamp
        chatData.mProfilePic = data.profilePic!!
        chatData.mFirstName = data.sender.firstName
        chatData.mThumbnailImage =
            if (data.messageType == "file") data.thumbnailImage else ""
        chatData.mPhoneNumber = data.sender.mPhoneNumber
        chatData.mLastName = data.sender.lastName
        chatData.mName = "${data.sender.firstName} ${data.sender.lastName}"
        chatData.mUserId = data.sender.userId
        chatData.isSentByMyself = isSentMyself ?: false
        chatData.mLastSentMsgTimeStamp = ""
        chatData.mIsRead = false
        chat.mData.add(chatData)
        return chat
    }

    private val chatWebSocketBroadcast: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onReceive(
            context: Context?, intent: Intent
        ) {
            if (intent.action == "chat_web_socket_message") {
                val type = intent.getStringExtra("type")
                val value = intent.getStringExtra("value")
                val isSent = intent.getBooleanExtra("isSentMyself", true)
                if (type != "connect") {
                    val gson = Gson()
                    if (isSent) {
                        val receivedChat = gson.fromJson(value, MyMessage::class.java)
                        val chat = receivedChat.toChatModel(true)
                        binding.chatdata!!.mData.addAll(0, chat.mData)
                    } else {
                        val receivedChat = gson.fromJson(value, OthersMessage::class.java)
                        if (receivedChat.data.groupId == groupId) {
                            val chat = receivedChat.toChat(false)
                            binding.chatdata!!.mData.addAll(0, chat.mData)


                        }
                    }
                    binding.chatRecyclerView.adapter!!.notifyDataSetChanged()
                    scrollToBottom()
                }
            }

        }
    }


    override fun onAudioRecordingCancelled(path: String?) {

    }

    override fun onAudioRecordingCompleted(path: Uri?) {
        if (path != null) {
            fileUpload(path)
        } else
            AppDialogs.showToastshort(this, "couldn't find path for audio recordings")
    }

    override fun showFailedMessage(msg: String) {
        runOnUiThread {
            AppDialogs.showToastDialog(this, msg)
        }

    }


    @SuppressLint("SetTextI18n")
    override fun changeProgress(progress: Int) {
        runOnUiThread {
            if (progress in 1..100) {
                binding.uploadProgressBar.visibility = View.VISIBLE
                binding.uploadText.visibility = View.VISIBLE
                binding.uploadProgressBar.progress = progress
                binding.uploadText.text = "$progress%"
                binding.btnPlus.visibility = View.GONE
            }
        }
    }

    override fun errorHappened() {
        runOnUiThread {
            binding.uploadProgressBar.visibility = View.GONE
            binding.uploadText.visibility = View.GONE
            binding.btnPlus.visibility = View.VISIBLE
            AppDialogs.showToastDialog(this, "Something happened couldn't upload the file")
        }


    }

    override fun completedSuccessfully() {
        runOnUiThread {
            binding.uploadProgressBar.visibility = View.GONE
            binding.uploadText.visibility = View.GONE
            binding.btnPlus.visibility = View.VISIBLE
        }
    }

    private fun openImagePicker() {
        imagePickerLauncher.launch("image/*")
    }

    private fun openVideoPicker() {
        videoPickerLauncher.launch("video/*")
    }

    private fun openAudioPicker() {
        audioPickerLauncher.launch("audio/*")
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST && mDialogAction == "IMAGE") {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST && mDialogAction == "CAMERA") {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == VIDEO_CAPTURE_REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED && mDialogAction == "CAPTURE_VIDEO"
        ) {
            if (PermissionChecker().checkPermission(this, Manifest.permission.CAMERA)) {
                dispatchTakeVideoIntent(this)
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            }
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }

        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST && mDialogAction == "VIDEO") {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openVideoPicker()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (mDialogAction == "CAPTURE_VIDEO") {
                dispatchTakeVideoIntent(this)
            }
            if (mDialogAction == "CAMERA") {
                dispatchTakePictureIntent()
            }
        }
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST && mDialogAction == "AUDIO") {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAudioPicker()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onScrollToDown() {
        if (!scrolledToTop)
            scrollToBottom()
    }
}