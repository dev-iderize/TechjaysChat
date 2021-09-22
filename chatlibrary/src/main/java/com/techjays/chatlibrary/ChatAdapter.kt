package com.techjays.chatlibrary

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.techjays.chatlibrary.Util.Utility
import com.techjays.chatlibrary.model.ChatList
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

/**
 * Created by Srinath on 17/09/21.
 **/

class ChatAdapter(
    val mContext: FragmentActivity,
    val mData: ArrayList<ChatList>,
    private var mCallback: Callback?
) : RecyclerView.Adapter<ChatAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.inflate_chat_list, parent, false)
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
        holder.mChatName.text = "${chatList.mFirstName}${chatList.mCompanyName}"
        holder.mChatMessage.text = chatList.mMessage
        Utility.loadUserImage(
            chatList.mProfilePic,
            holder.mUserImage,
            mContext
        )
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
            return@setOnLongClickListener true
        }

        holder.mChatCheckBox.setOnClickListener {
            chatList.isChecked = !chatList.isChecked
        }
    }


    override fun getItemCount(): Int {
        return mData.size
    }

    open class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mCardView: CardView = view.findViewById(R.id.card_view)
        var mUserImage: CircleImageView = view.findViewById(R.id.user_image)
        var mChatName: TextView = view.findViewById(R.id.chat_name)
        var mChatMessage: TextView = view.findViewById(R.id.chat_msg)
        var mChatCheckBox: CheckBox = view.findViewById(R.id.checkbox)
    }

    interface Callback {
        fun initChatMessage(selectedChat: ChatList)
        fun initDelete()
    }

}