package com.techjays.chatlibrary.fragments.follow

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.api.LibAppServices
import com.techjays.chatlibrary.chat.LibChatActivity
import com.techjays.chatlibrary.model.LibChatList
import com.techjays.chatlibrary.util.AppDialogs
import com.techjays.chatlibrary.util.EndlessRecyclerViewScrollListener
import com.techjays.chatlibrary.util.Utility
import com.techjays.chatlibrary.util.Utility.getETValue
import com.techjays.chatlibrary.viewmodel.ProfileViewModel
import java.util.*


class FollowingsList : DialogFragment(),
    FollowListAdapter.Callback, TextWatcher {

    private lateinit var mView: View

    private lateinit var mSwipe: SwipeRefreshLayout

    private lateinit var mFollowRecycler: RecyclerView
    private lateinit var mFollowListAdapter: FollowListAdapter

    private lateinit var mSearch: EditText
    private lateinit var mSearchClear: ImageView
    private lateinit var mSearchCancel: TextView
    private lateinit var mBack: ImageView
    var mData = ArrayList<LibChatList>()
    private lateinit var mViewModel: ProfileViewModel
    var mFollowingOffset = 0
    var mFollowingLimit = 10
    var isNextLink = false
    var mFollowings = ArrayList<LibChatList>()
    private lateinit var mListener: EndlessRecyclerViewScrollListener

    companion object {
        var TAG: String = FollowingsList::class.java.simpleName

        private lateinit var mSearch: EditText
        private lateinit var mCallback: Callback


        fun newInstance(callback: Callback): FollowingsList {
            mCallback = callback

            return FollowingsList()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_follows, container, false)
        init(mView)
        return mView
    }


    private fun init(view: View) {
        mFollowRecycler = view.findViewById(R.id.follows_recycler)
        mViewModel = ProfileViewModel(requireActivity())

        mSwipe = view.findViewById(R.id.swipe)

        mSearchClear = view.findViewById(R.id.layout_search_clear)
        mSearch = view.findViewById(R.id.layout_search_text)
        mSearchCancel = view.findViewById(R.id.layout_search_cancel)
        mBack = view.findViewById(R.id.back)

        clickListener()
        initRecycler()
        initObserver(false)
    }

    private fun getFollowList() {
        if (checkInternet()) {
            mViewModel.followsList(
                getETValue(mSearch), true,
                mFollowingOffset,
                mFollowingLimit
            )
        }
    }

    fun checkInternet(): Boolean {
        return Utility.isInternetAvailable(requireActivity())
    }

    private fun initObserver(show: Boolean) {
        getFollowList()
        mSwipe.isRefreshing = show
        if (!mViewModel.getFollowingsObserver().hasActiveObservers()) {
            mViewModel.getFollowingsObserver().observe(requireActivity(), {
                if (it.requestType == LibAppServices.API.following_list.hashCode()) {
                    isNextLink = (it as LibChatList).mNextLink
                    if (mFollowingOffset == 0)
                        mFollowings.clear()
                    mFollowings.addAll(it.mData)
                    mFollowListAdapter.notifyDataSetChanged()
                }
                mSwipe.isRefreshing = false
            })
        }
    }

    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(requireActivity())
        mFollowRecycler.layoutManager = layoutManager
        mFollowListAdapter = FollowListAdapter(requireActivity(), mFollowings, this)
        mFollowRecycler.adapter = mFollowListAdapter

        mListener = object : EndlessRecyclerViewScrollListener(layoutManager, 10) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                try {
                    if (isNextLink) {
                        mFollowingOffset += mFollowingLimit
                        getFollowList()
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }

        mSwipe.setOnRefreshListener {
            refresh()
            mSearch.clearFocus()
            mSearchCancel.performClick()
            mSearchClear.performClick()
            AppDialogs.hideSoftKeyboard(requireActivity(), mSearch)
        }
        mFollowRecycler.addOnScrollListener(mListener)
    }

    private fun clickListener() {
        mSearchClear.setOnClickListener {
            mSearch.setText("")
            mSearch.clearFocus()
        }
        mBack.setOnClickListener {
            dialog?.dismiss()
        }
        mSearch.addTextChangedListener(this)
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialog
    }

    @SuppressLint("ResourceAsColor")
    override fun onStart() {
        super.onStart()
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    private fun refresh() {
        if (checkInternet()) {
            mFollowingOffset = 0
            mListener.resetState()
            getFollowList()
            mSwipe.isRefreshing = false
        } else {
            mSwipe.isRefreshing = false
        }
    }


    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (getETValue(mSearch).isNotEmpty()) {
            mSearchClear.visibility = View.VISIBLE
        } else {
            mSearchClear.visibility = View.GONE
            AppDialogs.hideSoftKeyboard(requireActivity(), mSearch)
        }
        refresh()
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    interface Callback {
    }


    override fun onDestroyView() {
        super.onDestroyView()
        try {
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun selectUser(user: LibChatList) {
        if (mData.size > 0) {
            try {
                mData[mData.indexOf(user)].newMessage = false
                mFollowListAdapter.notifyDataSetChanged();
            } catch (e: Exception) {
                throw e
            }
        }
        val bundle = Bundle()
        val i = Intent(requireActivity(), LibChatActivity::class.java)
        bundle.putSerializable("chat_user", user)
        i.putExtras(bundle)
        startActivity(i)
    }


}