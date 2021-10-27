package com.techjays.chatlibrary.fragments.follow

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.techjays.chatlibrary.base.LibBaseFragment
import com.techjays.chatlibrary.model.Follow
import com.techjays.chatlibrary.util.AppDialogs
import com.techjays.chatlibrary.util.EndlessRecyclerViewScrollListener
import com.techjays.chatlibrary.util.Utility
import com.techjays.chatlibrary.util.Utility.getETValue
import com.techjays.chatlibrary.viewmodel.ProfileViewModel
import java.util.*
import android.util.DisplayMetrics
import android.widget.Toast
import com.google.gson.Gson
import com.techjays.chatlibrary.chat.LibChatActivity
import com.techjays.chatlibrary.model.LibChatList


class Followings : DialogFragment(),
    FollowAdapter.Callback, TextWatcher {

    private lateinit var mView: View

    private lateinit var mSwipe: SwipeRefreshLayout

    private lateinit var mFollowRecycler: RecyclerView
    private lateinit var mFollowAdapter: FollowAdapter

    private lateinit var mSearch: EditText
    private lateinit var mSearchClear: ImageView
    private lateinit var mSearchCancel: TextView
    private lateinit var mBack: TextView
    var mData = ArrayList<LibChatList>()
    private lateinit var mViewModel: ProfileViewModel
    var mFollowingOffset = 0
    var mFollowingLimit = 10
    var isNextLink = false
    var mFollowings = ArrayList<LibChatList>()
    private lateinit var mListener: EndlessRecyclerViewScrollListener

    companion object {
        var TAG: String = Followings::class.java.simpleName

        private lateinit var mSearch: EditText
        private lateinit var mCallback: Callback
        var mUserId = ""


        fun newInstance(userId: String, callback: Callback): Followings {
            mUserId = userId
            mCallback = callback

            return Followings()

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
        mBack = view.findViewById(R.id.back_arrow)

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
                    mFollowAdapter.notifyDataSetChanged()
                }
                mSwipe.isRefreshing = false
            })
        }
    }

    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(requireActivity())
        mFollowRecycler.layoutManager = layoutManager
        mFollowAdapter = FollowAdapter(requireActivity(), mFollowings, false, 1, this)
        mFollowRecycler.adapter = mFollowAdapter

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
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val deviceScreenHeight = displayMetrics.heightPixels

        // Change height child At index zero

        // Change height child At index zero
        (dialog!!.window!!.decorView.rootView as ViewGroup).getChildAt(0).layoutParams.height =
            deviceScreenHeight
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
        if (getETValue(mSearch).isNotEmpty())
            mSearchClear.visibility = View.VISIBLE
        else mSearchClear.visibility = View.GONE
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

    override fun selectUser(user: LibChatList) {
        if (mData.size > 0) {
            try {
                mData[mData.indexOf(user)].newMessage = false
                mFollowAdapter
                    .notifyDataSetChanged();
            } catch (e: Exception) {
                throw e
            }
        }
        val b = Bundle()
        val send = Intent(requireActivity(), LibChatActivity::class.java)
        b.putSerializable("chat_user", user)
        send.putExtras(b)
        startActivity(send)
    }


}