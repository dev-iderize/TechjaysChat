package com.techjays.chatlibrary

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.techjays.chatlibrary.Util.Utility
import com.techjays.chatlibrary.model.LibChatList
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
//        holder.mChatCheckBox.visibility = if (chatList.showCheckBox) View.VISIBLE else View.GONE
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

//        holder.mChatCheckBox.isChecked = chatList.isChecked

        holder.mCardView.setOnLongClickListener {
            for (i in mData) {
                i.isChecked = false
                i.showCheckBox = !i.showCheckBox
                notifyDataSetChanged()
            }
            mCallback?.initDelete()
            return@setOnLongClickListener true
        }

//        holder.mChatCheckBox.setOnClickListener {
//            chatList.isChecked = !chatList.isChecked
//        }
    }


    override fun getItemCount(): Int {
        return mData.size
    }

    open class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mCardView: CardView = view.findViewById(R.id.card_view)
        var mUserImage: CircleImageView = view.findViewById(R.id.user_image)
        var mChatName: TextView = view.findViewById(R.id.chat_name)
        var mChatMessage: TextView = view.findViewById(R.id.chat_msg)
//        var mChatCheckBox = view.findViewById<CheckBox>(R.id.check_box)
    }

    interface Callback {
        fun initChatMessage(selectedLibChat: LibChatList)
        fun initDelete()
    }

}