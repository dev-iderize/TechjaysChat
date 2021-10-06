package com.techjays.chatlibrary.chat

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.techjays.chatlibrary.ChatLibrary
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.model.LibChatMessages
import com.techjays.chatlibrary.util.DateUtil
import com.techjays.chatlibrary.util.Utility
import java.util.*

/**
 * Created by Srinath on 21/09/21.
 **/

class LibChatAdapter(
    val mContext: FragmentActivity,
    val mData: ArrayList<LibChatMessages>,
    private var mCallback: Callback?
) : RecyclerView.Adapter<LibChatAdapter.ItemViewHolder>() {

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return if (viewType == MESSAGE_TYPE_RIGHT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.lib_item_right, parent, false)
            ItemViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.lib_item_left, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        //val isEmployer = LocalStorageSP.isEmployer(mContext)
        val chatList = mData[position]
        holder.mCheckBox.visibility = if (chatList.showCheckBox) View.VISIBLE else View.GONE
        holder.mCheckBox.isChecked = chatList.isChecked
        holder.mChatItem.setOnLongClickListener {
            for (i in mData) {
                i.isChecked = false
                i.showCheckBox = !i.showCheckBox
                notifyDataSetChanged()
            }
            mCallback?.showDeleteButton()
            if (mData[position].mIsSentByMyself)
                mCallback?.messageDeleteforAll(mData[position].mIsSentByMyself)
            true
        }
        holder.mCheckBox.setOnClickListener {
            chatList.isChecked = !chatList.isChecked
        }
        holder.txtUserName.text = chatList.mMessage
        holder.mChatTime.text = DateUtil.formatDisplayDate(
            DateUtil.convertUTCToDeviceTime(chatList.mTimeStamp),
            "yyyy-MM-dd'T'HH:mm:ss",
            "hh:mmaa, dd MMM"
        )
        try {
            if (mData[position].mIsSentByMyself) {
                holder.mBackgroundRight.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        Utility.getColor(
                            mContext,
                            if (ChatLibrary.instance.mColor == "#FF878E") R.color.app_pink else R.color.app_blue                      //color:int = Color.parasecolor()
                        ), BlendModeCompat.SRC_ATOP
                    )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    open class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUserName: TextView = view.findViewById(R.id.tvMessage)
        var mChatTime: TextView = view.findViewById(R.id.time)
        var mChatItem: LinearLayout = view.findViewById(R.id.message_layout)
        var mBackgroundRight: Drawable = mChatItem.background
        var mCheckBox: CheckBox = view.findViewById(R.id.check_box_delete)

    }

    override fun getItemViewType(position: Int): Int {
        return if (mData[position].mIsSentByMyself) {
            MESSAGE_TYPE_RIGHT
        } else {
            MESSAGE_TYPE_LEFT
        }
    }

    interface Callback {
        fun messageDeleteforAll(mIsSentByMyself: Boolean)
        fun showDeleteButton()
    }
}