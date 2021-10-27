package com.techjays.chatlibrary.fragments.follow

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.model.Follow
import com.techjays.chatlibrary.util.Utility
import de.hdodenhof.circleimageview.CircleImageView
import java.util.ArrayList

class FollowAdapter(
    var mContext: Context,
    var mData: ArrayList<Follow>,
    private var isSearch: Boolean,
    var mType: Int,
    var mCallback: Callback
) : RecyclerView.Adapter<FollowAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(
                R.layout.inflate_follows,
                parent,
                false
            )
        return ItemViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {

        val user = mData[position]
        holder.mUserFullName.text = String.format("%s %s", user.mUserFirstName, user.mUserLastName)
        holder.mUserName.text = String.format("@%s", user.mUserName)
        Utility.loadUserImage(user.mThumbPicture, holder.mUserImage,mContext)

        holder.mMainLayout.setOnClickListener {
            mCallback.selectUser()
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    open class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mUserImage: CircleImageView = view.findViewById(R.id.user_image)
        var mUserFullName: TextView = view.findViewById(R.id.user_full_name)
        var mUserName: TextView = view.findViewById(R.id.user_name)
        var mUserFollow: ImageView = view.findViewById(R.id.user_follow)
        var mMainLayout: LinearLayout = view.findViewById(R.id.main_layout)
    }

    interface Callback {
     fun selectUser()
    }
}
