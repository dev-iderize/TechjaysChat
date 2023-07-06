package com.techjays.chatlibrary.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.bold
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.constants.Constant
import com.techjays.chatlibrary.model.LibChatMessages
import com.techjays.chatlibrary.model.LibChatUserModel
import com.techjays.chatlibrary.preview.LibImagePreviewActivity
import com.techjays.chatlibrary.preview.LibVideoPreviewActivity
import com.techjays.chatlibrary.util.DateUtil
import com.techjays.chatlibrary.util.LibChatUtility
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by Srinath on 21/09/21.
 **/

class LibChatAdapter(
    val mContext: FragmentActivity,
    val mData: ArrayList<LibChatMessages>,
    val mChatData: LibChatUserModel,
    private var mCallback: Callback?
) : RecyclerView.Adapter<LibChatAdapter.ItemViewHolder>() {

    private val MESSAGE_TYPE_RECIEVED = 0
    private val MESSAGE_TYPE_SENT = 1
    private val DOCUMENT_TYPE_SENT = 3
    private val DOCUMENT_TYPE_RECIEVED = 4
    private val MESSAGE_TYPE_RECEVIED_IMAGE = 5
    private val MESSAGE_TYPE_SENT_IMAGE = 6
    private val MESSAGE_TYPE_RECIVED_VIDEO = 7
    private val MESSAGE_TYPE_SENT_VIDEO = 8
    private var isVisibleCheckbox = false
    private val MESSAGE_TYPE_NOTIFICATION = 9
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    when (viewType) {
                        MESSAGE_TYPE_SENT -> R.layout.lib_item_text_right
                        MESSAGE_TYPE_RECIEVED -> R.layout.lib_item_text_left
                        MESSAGE_TYPE_RECEVIED_IMAGE -> R.layout.lib_item_image_left
                        MESSAGE_TYPE_SENT_IMAGE -> R.layout.lib_item_image_right
                        MESSAGE_TYPE_RECIVED_VIDEO -> R.layout.lib_item_video_left
                        MESSAGE_TYPE_SENT_VIDEO -> R.layout.lib_item_video_right
                        else -> R.layout.lib_item_auto_notification
                    }, parent, false
                )
        return ItemViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: ItemViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val chatList = mData[position]

        holder.mCheckBox.visibility = if (isVisibleCheckbox) View.VISIBLE else View.GONE

        holder.mChatItem.setOnLongClickListener {
            mCallback?.clear()
            deleteInvisible()
            isVisibleCheckbox = !isVisibleCheckbox
            notifyDataSetChanged()
            true
        }

        holder.mChatItem.setOnClickListener {

            if (chatList.mFileType == Constant.CHAT_TYPE_IMAGE) {
                val i = Intent(mContext, LibImagePreviewActivity::class.java)
                i.putExtra("url_data", chatList.mMessage)
                mContext.startActivity(i)
            } else if (chatList.mFileType == Constant.CHAT_TYPE_VIDEO) {
                val i = Intent(mContext, LibVideoPreviewActivity::class.java)
                i.putExtra("url_data", chatList.mMessage)
                i.putExtra("preview", false)
                mContext.startActivity(i)
            }

        }
        holder.mCheckBox.isChecked = chatList.isChecked

        holder.mCheckBox.setOnClickListener {
            chatList.isChecked = !chatList.isChecked
            if (holder.mCheckBox.isChecked) {
                Constant.COUNTER_DELETE_CHECKBOX += 1
            } else
                Constant.COUNTER_DELETE_CHECKBOX -= 1

            mCallback?.showDeleteButton()
        }

        if (chatList.mIsSentByMyself) {
            LibChatUtility.loadUserImage(
                mChatData.mSenderProfilePicUrl,
                holder.mProfile,
                mContext
            )
            holder.mName.text = mChatData.mSenderFullName

        } else {
            LibChatUtility.loadUserImage(
                mChatData.mReceiverProfilePicUrl,
                holder.mProfile,
                mContext
            )
            holder.mName.text = mChatData.mReceiverFullName
        }
        if (chatList.mMessageType == Constant.CHAT_TYPE_NOTIFICATION) {
            val first = chatList.mMessage.split(" ")[0]
            holder.txMessage.text =
                SpannableStringBuilder().bold {
                    append(if (chatList.mIsSentByMyself) "You" else mChatData.mReceiverFullName)
                }.append(if (chatList.mIsSentByMyself) " have " else " has ")
                    .bold {
                        append(first)
                    }.append(chatList.mMessage.replace(first, ""))
        } else
            holder.txMessage.text = chatList.mMessage
        when (chatList.mFileType) {
            Constant.CHAT_TYPE_IMAGE_, Constant.CHAT_TYPE_IMAGE,Constant.CHAT_TYPE_VIDEO -> {
                LibChatUtility.loadUserImageWithCache2(
                    chatList.mMessage,
                    holder.mThumbNail,
                    mContext
                )
            }
        }

        holder.mTime.text = DateUtil.formatDisplayDate(
            DateUtil.convertUTCToDeviceTime(chatList.mTimeStamp),
            "yyyy-MM-dd'T'HH:mm:ss",
            "MMM dd, hh:mm aa"
        )

        /*holder.mTime.text = DateUtil.formatDisplayDate(
            DateUtil.convertUTCToDeviceTime(chatList.mTimeStamp),
            "yyyy-MM-dd'T'HH:mm:ss",
            "hh:mmaa, dd MMM"
        )*/
        try {
            /*if (mData[position].mIsSentByMyself) {
                holder.mBackgroundRight.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        Utility.getColor(
                            mContext,
                            if (ChatLibrary.instance.mColor == "#FF878E") R.color.app_pink else R.color.app_blue                      //color:int = Color.parasecolor()
                        ), BlendModeCompat.SRC_ATOP
                    )
            }*/
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    private fun deleteInvisible() {
        Constant.COUNTER_DELETE_CHECKBOX = 0
        mCallback?.showDeleteButton()
    }

    open class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txMessage: TextView = view.findViewById(R.id.tv_message)
        var mName: TextView = view.findViewById(R.id.tv_name)
        var mChatItem: LinearLayout = view.findViewById(R.id.message_layout)
        var mBackgroundRight: Drawable = mChatItem.background
        var mCheckBox: CheckBox = view.findViewById(R.id.check_box_delete)
        var mProfile: CircleImageView = view.findViewById(R.id.userImage)
        var mThumbNail: ImageView = view.findViewById(R.id.lib_thumbnail)
        var mTime: TextView = view.findViewById(R.id.tv_time)

    }

    override fun getItemViewType(position: Int): Int {
        return if (mData[position].mIsSentByMyself) {
            var type = 1
            type = when (mData[position].mMessageType) {
                Constant.CHAT_TYPE_NOTIFICATION -> {
                    MESSAGE_TYPE_NOTIFICATION
                }

                Constant.CHAT_TYPE_MESSAGE -> {
                    MESSAGE_TYPE_SENT
                }

                else -> {
                    LibChatUtility.log(mData[position].mMessageType)
                    if (mData[position].mFileType == Constant.CHAT_TYPE_IMAGE || mData[position].mFileType == Constant.CHAT_TYPE_IMAGE_) {
                        MESSAGE_TYPE_SENT_IMAGE
                    } else {
                        MESSAGE_TYPE_SENT_VIDEO
                    }
                }
            }
            type
        } else {
            var type = 0
            type = when (mData[position].mMessageType) {
                Constant.CHAT_TYPE_NOTIFICATION -> {
                    MESSAGE_TYPE_NOTIFICATION
                }

                Constant.CHAT_TYPE_MESSAGE -> {
                    MESSAGE_TYPE_RECIEVED
                }

                else -> {
                    if (mData[position].mFileType == Constant.CHAT_TYPE_IMAGE || mData[position].mFileType == Constant.CHAT_TYPE_IMAGE_) {
                        MESSAGE_TYPE_RECEVIED_IMAGE
                    } else {
                        MESSAGE_TYPE_RECIVED_VIDEO
                    }
                }
            }
            type
        }
    }


    interface Callback {
        fun showDeleteButton()
        fun clear()
    }
}