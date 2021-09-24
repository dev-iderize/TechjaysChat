package com.techjays.chatlibrary.chatlist

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
import com.techjays.chatlibrary.model.LibUser
import com.techjays.chatlibrary.viewmodel.LibChatViewModel
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

    override fun onBackPressed() {
    }

    override fun onResumeFragment() {

    }

    override fun initChatMessage(selectedLibChat: LibChatList) {
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