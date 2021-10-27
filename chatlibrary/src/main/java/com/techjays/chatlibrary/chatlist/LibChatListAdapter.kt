package com.techjays.chatlibrary.chatlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.curioustechizen.ago.RelativeTimeTextView
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.util.Utility
import com.techjays.chatlibrary.model.LibChatList
import com.techjays.chatlibrary.util.DateUtil
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

/**
 * Created by Srinath on 17/09/21.
 **/

class LibChatListAdapter(
    val mContext: FragmentActivity,
    val mData: ArrayList<LibChatList>,
    private var mCallback: Callback?
) : RecyclerView.Adapter<LibChatListAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.lib_inflate_chat_list, parent, false)
        return ItemViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: ItemViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        //val isEmployer = LocalStorageSP.isEmployer(mContext)

        val chatList = mData[position]
        holder.mChatCheckBox.visibility = if (chatList.showCheckBox) View.VISIBLE else View.GONE
        holder.mChatName.text = "${chatList.mFirstName}${chatList.mCompanyName}${" "}${chatList.mLastName}"
        holder.mChatMessage.text = chatList.mMessage

        if (chatList.newMessage)
            holder.mIndicator.visibility = View.VISIBLE
        else
            holder.mIndicator.visibility = View.GONE
        if (chatList.mProfilePic != null)
            Utility.loadUserImage(
                chatList.mProfilePic,
                holder.mUserImage,
                mContext
            )
        else
            Utility.loadPlaceholder(R.drawable.lib_ic_user_placeholder, holder.mUserImage)
        holder.mCardView.setOnClickListener {
            mCallback?.initChatMessage(chatList)
        }

        holder.mChatCheckBox.isChecked = chatList.isChecked

        holder.mCardView.setOnLongClickListener {
            for (i in mData) {
                i.isChecked = false
                i.showCheckBox = !i.showCheckBox
                notifyDataSetChanged()
            }
            mCallback?.initDelete()
            return@setOnLongClickListener true
        }

        holder.mChatCheckBox.setOnClickListener {
            chatList.isChecked = !chatList.isChecked
        }
        holder.mTime.setReferenceTime(DateUtil.convertToRelativeTime(chatList.mTimeStamp))
    }


    override fun getItemCount(): Int {
        return mData.size
    }

    open class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mCardView: CardView = view.findViewById(R.id.card_view)
        var mUserImage: CircleImageView = view.findViewById(R.id.user_image)
        var mChatName: TextView = view.findViewById(R.id.chat_name)
        var mChatMessage: TextView = view.findViewById(R.id.chat_msg)
        var mChatCheckBox = view.findViewById<CheckBox>(R.id.check_box_)
        var mIndicator = view.findViewById<RelativeLayout>(R.id.lib_new_indicator)
        var mTime = view.findViewById<RelativeTimeTextView>(R.id.chat_time)

    }

    interface Callback {
        fun initChatMessage(selectedLibChat: LibChatList)
        fun initDelete()
    }

}