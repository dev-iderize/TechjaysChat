package com.techjays.chatlibrary.chatlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.techjays.chatlibrary.ChatLibrary
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.databinding.InflateChatListBinding
import com.techjays.chatlibrary.model.ChatList


class ChatListAdapter(
    private val mContext: LibChatListFragment,
    private val mListData: ArrayList<ChatList.ChatListData>
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
        holder.bind(eachListData, myUserId)
    }

    fun updateItem(chat: ChatList.ChatListData) {
        val index = mListData.indexOfFirst { it.mGroupId == chat.mGroupId }
        if (index != -1) {
            chat.mGroupName = mListData[index].mGroupName
            chat.mCreatorId = mListData[index].mCreatorId
            chat.mIsSentByMyself = mListData[index].mIsSentByMyself
            mListData.removeAt(index)
            mListData.add(0, chat)
            notifyItemMoved(index, 0)
            notifyItemChanged(0)
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

        fun bind(data: ChatList.ChatListData, myUserId: Int) {
            binding.listData = data
            binding.isMyself = myUserId == data.mCreatorId
            val haveNewMessage = !data.mIsRead
            binding.haveNewMessage = haveNewMessage
            binding.executePendingBindings()
        }
    }
}