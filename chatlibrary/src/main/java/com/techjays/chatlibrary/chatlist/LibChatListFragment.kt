package com.techjays.chatlibrary.chatlist

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.techjays.chatlibrary.ChatLibrary
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.util.AppDialogs
import com.techjays.chatlibrary.util.EndlessRecyclerViewScrollListener
import com.techjays.chatlibrary.api.LibAppServices
import com.techjays.chatlibrary.api.Response
import com.techjays.chatlibrary.api.ResponseListener
import com.techjays.chatlibrary.base.LibBaseFragment
import com.techjays.chatlibrary.chat.LibChatActivity
import com.techjays.chatlibrary.databinding.LibActivityChatBinding
import com.techjays.chatlibrary.databinding.LibActivityChatListBinding
import com.techjays.chatlibrary.model.ChatList
import com.techjays.chatlibrary.model.LibChatList
import com.techjays.chatlibrary.model.LibChatSocketMessages
import com.techjays.chatlibrary.model.OthersMessage
import com.techjays.chatlibrary.model.User
import com.techjays.chatlibrary.util.ChatSocketListener
import com.techjays.chatlibrary.util.Utility
import com.techjays.chatlibrary.viewmodel.LibChatViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.ArrayList

class LibChatListFragment : Fragment(), ResponseListener, ChatSocketListener.SocketCallback,
    ChatListAdapter.ChatListCallback {

    lateinit var binding: LibActivityChatListBinding
    lateinit var mListener: EndlessRecyclerViewScrollListener
    var mOffset = 0
    var mLimit = 10
    var mNextLink = false
    private var ws: WebSocket? = null

    private lateinit var listener: ChatSocketListener
    private lateinit var client: OkHttpClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.lib_activity_chat_list, container, false)
        return binding.root
    }

    private fun initBundle(token: String) {
        val bundle = arguments
        if (bundle != null) {


            val baseURL = bundle.getString("base_url")!!
            val socketUrl = bundle.getString("socket_url")!!
            //  val chatToken = bundle.getString("chat_token")!!
            val authToken = bundle.getString("auth_token")!!
            val userId = bundle.getInt("user_id")
            val phoneNumber = bundle.getString("phone_number")!!

            Log.e("baseurl", bundle.getString("base_url")!!)
            Log.e("socket_", bundle.getString("socket_url")!!)
            Log.e("auth_token", bundle.getString("auth_token")!!)
            Log.e("muserId", userId.toString())
            ChatLibrary.instance.authToken = authToken
            ChatLibrary.instance.chatToken = token
            ChatLibrary.instance.baseUrl = baseURL
            ChatLibrary.instance.socketUrl = socketUrl
            ChatLibrary.instance.mUserId = userId
            ChatLibrary.instance.mPhoneNumber = phoneNumber

        }
        init()
    }

    override fun onResume() {
        super.onResume()
        val bundle = arguments
        if (bundle != null) {
            val authToken = bundle.getString("auth_token")!!
            if (Utility.checkInternet(requireContext())) {
                Log.e("auth_token", authToken)
                ChatLibrary.instance.authToken = authToken
                LibAppServices.getWSToken(requireContext(), authToken, this)
            }
        }
    }


    fun navToChatActivity(
        groupId: Int,
        groupName: String,
        groupProfilePic: String,
        groupCreatorId: Int
    ) {
        val i = Intent(requireActivity(), LibChatActivity::class.java)
        i.putExtra("groupName", groupName)
        i.putExtra("groupProfilePic", groupProfilePic)
        i.putExtra("groupId", groupId)
        i.putExtra("creatorId", groupCreatorId)
        startActivity(i)
    }

    private fun initRecycler() {
        val recycler = binding.recyclerView
        recycler.layoutManager =
            LinearLayoutManager(requireActivity())
        val layoutManager = LinearLayoutManager(requireActivity())
        getChatList(mOffset, mLimit)
        recycler.layoutManager = layoutManager
        mListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                try {
                    if (mNextLink) {
                        mOffset += mLimit
                        getChatList(mOffset, mLimit)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
        recycler.addOnScrollListener(mListener)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun init() {
        binding.activity = this
        client = OkHttpClient()
        binding.isEmpty = false

        binding.swipe.setOnRefreshListener {
            binding.swipe.isRefreshing = false
            mListener.resetState()
            mOffset = 0
            binding.chatList!!.mData.clear()
            getChatList(mOffset, mLimit)

        }
        binding.header.setOnLongClickListener {
            AppDialogs.showToastDialog(requireContext(), ChatLibrary.instance.baseUrl)
            return@setOnLongClickListener true
        }
        listener = ChatSocketListener(requireContext(), ws, this)
        requireActivity().registerReceiver(
            chatWebSocketBroadcast, IntentFilter("chat_web_socket_message")
        )
        initRecycler()
        webSocketStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        listener.cancel()
        requireActivity().unregisterReceiver(chatWebSocketBroadcast)
    }

    private fun webSocketStart() {
        val request: Request =
            Request.Builder().url(ChatLibrary.instance.socketUrl).build()
        ws = client.newWebSocket(request, listener)
        listener.initialize(ws!!)

    }


    fun getChatList(offset: Int, limit: Int) {
        if (Utility.checkInternet(requireContext())) {
            LibAppServices.getChatList(requireContext(), offset, limit, this)
        }
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
                    if (!isSent) {
                        val gson = Gson()
                        val receivedChat = gson.fromJson(value, OthersMessage::class.java)
                        val adapter = binding.recyclerView.adapter as ChatListAdapter
                        val chat = receivedChat.toChatList(isSent)
                        for (newChat in chat.mData) {
                            adapter.updateItem(newChat)
                        }
                        binding.recyclerView.adapter!!.notifyDataSetChanged()
                    }

                }
                //

            }
        }

    }

    fun OthersMessage.toChatList(isSentMyself: Boolean): ChatList {
        val chat = ChatList()
        val chatData = ChatList.ChatListData()

        chatData.mMessageId = data.messageId
        chatData.mGroupId = data.groupId
        chatData.mFileType = data.fileType
        chatData.mMessage = data.message
        chatData.mTime = data.timestamp
        chatData.mMessage = data.message
        chatData.mMessageType = data.messageType
        chatData.mPhoneNumber = data.sender.mPhoneNumber
        chatData.mDisplayPicture = if (data.profilePic.isNullOrEmpty()) "" else data.profilePic
        chatData.mLastSentMsgTimeStamp = data.timestamp
        //  chatData.mCreatorId = data.sender.userId
        chatData.isSentByMyself = isSentMyself
        chatData.mLastSentMsgTimeStamp = ""
        chatData.mIsRead = false
        chat.mData.add(chatData)
        return chat
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResponse(r: Response?) {
        if (r != null) {
            when (r.requestType) {
                LibAppServices.API.get_chat_list.hashCode() -> {
                    //context.binding.isLoading = false
                    if (r.responseStatus!!) {
                        val chatList = (r as ChatList)
                        mNextLink = chatList.next_link
                        if (mOffset == 0) {
                            binding.chatList = chatList

                            binding.isEmpty = r.mData.size == 0

                            binding.recyclerView.adapter =
                                ChatListAdapter(
                                    this@LibChatListFragment,
                                    r.mData, this
                                )
                        } else {
                            binding.chatList?.mData?.addAll(
                                chatList.mData
                            )
                            binding.recyclerView.adapter?.notifyDataSetChanged()
                        }
                    } else
                        Toast.makeText(requireContext(), r.responseMessage, Toast.LENGTH_LONG)
                            .show()
                }

                LibAppServices.API.web_socket_token.hashCode() -> {
                    if (r.responseStatus!!) {
                        val data = r as User
                        ChatLibrary.instance.chatToken = data.mUser?.mUserToken!!
                        initBundle(data.mUser?.mUserToken!!)
                    }
                }
            }

        }
    }

    override fun showFailedMessage(msg: String) {

        requireActivity().runOnUiThread {
            AppDialogs.showToastDialog(requireContext(), msg)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun reset() {
        val adapter = binding.recyclerView.adapter as ChatListAdapter
        binding.chatList!!.mData.clear()
        adapter.notifyDataSetChanged()
        mOffset = 0
        mListener.resetState()
        getChatList(mOffset, mLimit)

    }
}