package com.techjays.chatlibrary.chatlist

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.techjays.chatlibrary.ChatLibrary
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.util.AppDialogs
import com.techjays.chatlibrary.util.EndlessRecyclerViewScrollListener
import com.techjays.chatlibrary.api.LibAppServices
import com.techjays.chatlibrary.base.LibBaseFragment
import com.techjays.chatlibrary.chat.LibChatActivity
import com.techjays.chatlibrary.model.LibChatList
import com.techjays.chatlibrary.model.LibChatSocketMessages
import com.techjays.chatlibrary.model.LibUser
import com.techjays.chatlibrary.util.ChatSocketListener
import com.techjays.chatlibrary.viewmodel.LibChatViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [LibChatListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LibChatListFragment : LibBaseFragment(), LibChatListAdapter.Callback, View.OnClickListener {

    private lateinit var mView: View

    private lateinit var mRecyclerView: RecyclerView
    var mOffset = 0
    var mLimit = 6
    var isNextLink = false
    private lateinit var mListener: EndlessRecyclerViewScrollListener
    private lateinit var mSwipe: SwipeRefreshLayout
    private lateinit var mLibChatViewModel: LibChatViewModel
    var mData = ArrayList<LibChatList>()
    private lateinit var mListAdapterLib: LibChatListAdapter
    private var ws: WebSocket? = null
    private lateinit var listener: ChatSocketListener
    private lateinit var mDelete: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.lib_activity_chat_list, container, false)
        init(mView)
        return mView
    }

    override fun init(view: View) {
        mLibChatViewModel = LibChatViewModel(this.requireActivity())

        mRecyclerView = mView.findViewById(R.id.recycler_chat_list)
        mSwipe = view.findViewById(R.id.chat_swipe)
        mDelete = view.findViewById(R.id.delete_button)
        initBundle()
    }

    override fun initBundle() {
        val bundle = arguments
        if (bundle != null) {

            val baseURL = bundle.getString("base_url")!!
            val chatToken = bundle.getString("chat_token")!!
            val authToken = bundle.getString("auth_token")!!
            val userData = Gson().fromJson(bundle.getString("user_data"), LibUser::class.java)

            if (bundle.containsKey("chat_user_data")) {
                val chatUserData =
                    Gson().fromJson(
                        bundle.getString("chat_user_data").toString(),
                        LibUser::class.java
                    )
                val chatData = LibChatList()
                chatData.mProfilePic = chatUserData.mProfilePic
                chatData.mCompanyName = chatUserData.mUserName
                chatData.mToUserId = chatUserData.mUserId.toString()
                initChatMessage(chatData)
            }

            ChatLibrary.instance.authToken = authToken
            ChatLibrary.instance.chatToken = chatToken
            ChatLibrary.instance.baseUrl = baseURL
            ChatLibrary.instance.mUserData = userData

            initRecycler()
            getChatList(true)
            clickListener()
            initObserver()
        }
    }

    private fun initObserver() {
        if (!mLibChatViewModel.getChatObserver().hasActiveObservers()) {
            mLibChatViewModel.getChatObserver().observe(requireActivity(), {
                AppDialogs.hideProgressDialog()
                mSwipe.isRefreshing = false
                when (it?.requestType) {
                    LibAppServices.API.chat_list.hashCode() -> {
                        if (it.responseStatus!!) {
                            isNextLink = (it as LibChatList).mNextLink
                            if (mOffset == 0)
                                mData.clear()
                            mData.addAll(it.mData)
                            mListAdapterLib.notifyDataSetChanged()
                        } else
                            AppDialogs.customOkAction(requireContext(), it.responseMessage)
                    }

                    LibAppServices.API.delete_chats.hashCode() -> {
                        if (it.responseStatus!!) {
                            val iterator = mData.iterator()
                            while (iterator.hasNext()) {
                                val item = iterator.next()
                                if (item.isChecked) {
                                    iterator.remove()
                                }
                            }
                            mListAdapterLib.notifyDataSetChanged()
                        } else AppDialogs.showSnackbar(mRecyclerView, it.responseMessage)
                    }
                }
            })
        }
    }

    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.layoutManager = layoutManager
        mListAdapterLib = LibChatListAdapter(requireActivity(), mData, this)
        mRecyclerView.adapter = mListAdapterLib

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
                AppDialogs.showProgressDialog(requireContext())
            mLibChatViewModel.getChatList(mOffset, mLimit)
        }
    }

    override fun clickListener() {
        mSwipe.setOnRefreshListener {
            mListener.resetState()
            mOffset = 0
            getChatList(false)
            mDelete.visibility = View.GONE
        }
        mDelete.setOnClickListener(this)
    }

    private fun start() {
        val request: Request = Request.Builder().url("ws://3.19.93.161:8765").build()
        var client: OkHttpClient = OkHttpClient()
        listener = ChatSocketListener(object : ChatSocketListener.CallBack {
            override fun onMessageReceive(libChatMessage: LibChatSocketMessages) {
                requireActivity().runOnUiThread {
                    var isAlreadyInList = false
                    val isMySelf = libChatMessage.mData?.mSender == null
                    if (!isMySelf) {
                        for (item in mData) {
                            if (libChatMessage.mData?.mSender!!.mUserId.toString()
                                    .equals(item.mToUserId.toString())
                            ) {
                                isAlreadyInList = true
                                Log.e("te", mData.indexOf(item).toString());
                                mData[mData.indexOf(item)].mMessage =
                                    libChatMessage.mData?.mMessage!!
                                mData[mData.indexOf(item)].mProfilePic =
                                    libChatMessage.mData?.mProfilePic!!
                                mData[mData.indexOf(item)].newMessage = true
                                var newChat: LibChatList = mData[mData.indexOf(item)]
                                mData.remove(item)
                                mData.add(0, newChat)
                                mListAdapterLib.notifyDataSetChanged()
                                break
                            }
                        }

                        if (!isAlreadyInList) {
                            var newChat: LibChatList = LibChatList()
                            newChat.mMessage = libChatMessage.mData?.mMessage!!
                            newChat.mToUserId = libChatMessage.mData?.mSender!!.mUserId.toString()
                            newChat.mFirstName = libChatMessage.mData?.mSender!!.mUserName
                            newChat.mProfilePic = libChatMessage.mData?.mProfilePic!!
                            newChat.newMessage = true
                            mData.add(0, newChat)
                            mListAdapterLib.notifyDataSetChanged()
                        }
                    }

                }
            }
        })
        ws = client.newWebSocket(request, listener)
        client.dispatcher().executorService().shutdown()
    }

    override fun onResume() {
        super.onResume()
        start()
    }

    override fun onPause() {
        super.onPause()
        if (ws != null) {
            ws!!.cancel()
            ws = null;
        }
    }


    override fun onBackPressed() {
    }

    override fun onResumeFragment() {

    }

    override fun initChatMessage(selectedLibChat: LibChatList) {
        if(mData.size > 0){
            try {
                mData[mData.indexOf(selectedLibChat)].newMessage = false
                mListAdapterLib.notifyDataSetChanged();
            } catch (e: Exception) {
                throw e
            }
        }
        val i = Intent(requireActivity(), LibChatActivity::class.java)
        i.putExtra("chat_user", selectedLibChat)
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
                    id.add(i.mToUserId)
            }
            if (id.isNotEmpty())
                mLibChatViewModel.deleteChats(TextUtils.join(",", id))
            else AppDialogs.showSnackbar(mDelete, "Please select something!")
        }
    }
}