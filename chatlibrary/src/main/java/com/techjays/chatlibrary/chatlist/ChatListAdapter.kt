package com.techjays.chatlibrary.chatlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.techjays.chatlibrary.ChatLibrary
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.databinding.InflateChatListBinding
import com.techjays.chatlibrary.model.ChatList
import com.techjays.chatlibrary.util.Utility


class ChatListAdapter(
    private val mContext: LibChatListFragment,
    private val mListData: ArrayList<ChatList.ChatListData>,
    private var mCallBack: ChatListCallback
) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: InflateChatListBinding =
            DataBindingUtil.inflate(inflater, R.layout.inflate_chat_list, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eachListData = mListData[position]
        val myUserId = ChatLibrary.instance.mUserId
        holder.itemView.setOnClickListener {
            mContext.navToChatActivity(
                eachListData.mGroupId,
                eachListData.mGroupName, eachListData.mDisplayPicture,
                eachListData.mCreatorId
            )
        }
        holder.bind(eachListData, myUserId, mContext.requireContext())
    }

    fun updateItem(chat: ChatList.ChatListData) {
        val index = mListData.indexOfFirst { it.mGroupId == chat.mGroupId }
        if (index != -1) {
            chat.mGroupName = mListData[index].mGroupName
            chat.mCreatorId = mListData[index].mCreatorId
            chat.isSentByMyself = mListData[index].isSentByMyself
            val name = Utility.getLibContactName(
                chat.mPhoneNumber,
                mContext.requireContext()
            )
            if (name.isNotEmpty())
                mListData[index].mGroupName = "$name's Circle"
            mListData.removeAt(index)
            mListData.add(0, chat)
            notifyItemMoved(index, 0)
            notifyItemChanged(0)
        } else {
            mCallBack.reset()
        }
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(private val binding: InflateChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ChatList.ChatListData, myUserId: Int, context: Context) {
            val name = Utility.getLibContactName(
                data.mPhoneNumber,
                context
            )
            if (name.isNotEmpty())
                data.mGroupName = "$name's Circle"
            binding.listData = data
            binding.isMyself = myUserId == data.mCreatorId
            val haveNewMessage = !data.mIsRead
            binding.haveNewMessage = haveNewMessage
            binding.executePendingBindings()
        }
    }


    interface ChatListCallback {
        fun reset()
    }
}