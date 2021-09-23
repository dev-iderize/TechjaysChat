package com.techjays.chatlibrary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.techjays.chatlibrary.Util.AppDialogs
import com.techjays.chatlibrary.Util.EndlessRecyclerViewScrollListener
import com.techjays.chatlibrary.base.LibBaseFragment
import com.techjays.chatlibrary.model.LibChatList
import com.techjays.chatlibrary.view_model.LibChatViewModel
import java.util.ArrayList

private const val ARG_PARAM1 = "base_url"
private const val ARG_PARAM2 = "chat_token"
private const val ARG_PARAM3 = "auth_token"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatListFragmentLib.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatListFragmentLib : LibBaseFragment(), LibChatListAdapter.Callback {
    private var base_url: String? = null
    private var chat_token: String? = null
    private var auth_token: String? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            base_url = it.getString(ARG_PARAM1)
            chat_token = it.getString(ARG_PARAM2)
            auth_token = it.getString(ARG_PARAM3)

            try {
                ChatLibrary.instance.auth_token = auth_token!!
                ChatLibrary.instance.chat_token = chat_token!!
                ChatLibrary.instance.base_url = base_url!!
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView =  inflater.inflate(R.layout.lib_fragment_chat_list, container, false)
        init(mView)
        return mView
    }

    override fun init(view: View) {
        mLibChatViewModel = LibChatViewModel(this.requireActivity())
        mRecyclerView = mView.findViewById(R.id.recycler_chat_list)
        mSwipe = view.findViewById(R.id.chat_swipe)
        initRecycler()
        getChatList(true)
        clickListener()
    }

    private fun initRecycler() {

        val layoutManager = LinearLayoutManager(requireActivity())
        mRecyclerView.layoutManager = layoutManager
        mListAdapterLib = LibChatListAdapter(requireActivity(), mData,this)
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
                AppDialogs.showProgressDialog(requireActivity())
            mLibChatViewModel.getChatList(mOffset, mLimit)
            if (!mLibChatViewModel.getChatObserver().hasActiveObservers()) {
                mLibChatViewModel.getChatObserver().observe(requireActivity(), {
                    AppDialogs.hideProgressDialog()
                    mSwipe.isRefreshing = false
                    if (it?.responseStatus!!) {
                        isNextLink = (it as LibChatList).mNextLink
                        if (mOffset == 0)
                            mData.clear()
                        mData.addAll(it.mData)
                        mListAdapterLib.notifyDataSetChanged()
                    } else {
                        AppDialogs.customOkAction(requireActivity(), it?.responseMessage)
                        AppDialogs.hideProgressDialog()
                        mSwipe.isRefreshing = false
                    }
                })
            }
        }

    }

    override fun initBundle() {

    }

    override fun clickListener() {
        mSwipe.setOnRefreshListener {
            mListener.resetState()
            mOffset = 0
            getChatList(false)
        }
    }

    override fun onBackPressed() {
    }

    override fun onResumeFragment() {

    }

    companion object {
        @JvmStatic
        fun newInstance(base_url: String,chat_token: String,auth_token: String) =
            ChatListFragmentLib().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, base_url)
                    putString(ARG_PARAM2, chat_token)
                    putString(ARG_PARAM3, auth_token)
                }
            }
    }

    override fun initChatMessage(selectedLibChat: LibChatList) {

    }

    override fun initDelete() {
    }
}