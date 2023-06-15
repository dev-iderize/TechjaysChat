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
        /*  if (mContext.nodeMap.containsKey(eachListData.mCreatorId)) {
              eachListData.mGroupName =
                  mContext.nodeMap[eachListData.mCreatorId]?.name!! + "'s Circle"
          }*/
        val myUserId = ChatLibrary.instance.mUserId
        holder.itemView.setOnClickListener {
            mContext.navToChatActivity(
                eachListData.mGroupId,
                eachListData.mGroupName, eachListData.mDisplayPicture
            )
        }
        holder.bind(eachListData, myUserId)
    }

    fun updateItem(chat: ChatList.ChatListData) {
        val index = mListData.indexOfFirst { it.mGroupId == chat.mGroupId }
        if (index != -1) {
            chat.mCreatorId = mListData[index].mCreatorId
            mListData[index] = chat
            notifyItemChanged(index)
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