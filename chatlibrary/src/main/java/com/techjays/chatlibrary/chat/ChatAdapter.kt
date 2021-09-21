package com.techjays.chatlibrary.chat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.curioustechizen.ago.RelativeTimeTextView
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.Util.DateUtil
import com.techjays.chatlibrary.Util.Utility
import com.techjays.chatlibrary.model.ChatMessages
import java.text.SimpleDateFormat
import java.util.ArrayList

/**
 * Created by Srinath on 21/09/21.
 **/

class ChatAdapter(val mContext: FragmentActivity,
                  val mData: ArrayList<ChatMessages>)
    : RecyclerView.Adapter<ChatAdapter.ItemViewHolder>() {

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return if (viewType == MESSAGE_TYPE_RIGHT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_right, parent, false)
            ItemViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_left, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        //val isEmployer = LocalStorageSP.isEmployer(mContext)
        val chatList = mData[position]
        holder.txtUserName.text = chatList.mMessage
        holder.mChatTime.setReferenceTime(DateUtil.convertUTCToDeviceTime(chatList.mTimeStamp))
        /*val format = SimpleDateFormat("yyyyMMddhhmmss")
        val date= format.parse(chatList.mTimeStamp)
        val newFormat = SimpleDateFormat("( hh:mm aa ) dd MMM yy")
        holder.mTime.text = newFormat.format(date)*/
    }


    override fun getItemCount(): Int {
        return mData.size
    }

    open class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUserName: TextView = view.findViewById(R.id.tvMessage)
        var mChatTime: RelativeTimeTextView = view.findViewById(R.id.chat_time)

    }

    override fun getItemViewType(position: Int): Int {
        return if (mData[position].mIsSentByMyself) {
            MESSAGE_TYPE_RIGHT
        } else {
            MESSAGE_TYPE_LEFT
        }
    }
}