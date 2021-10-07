package com.techjays.chatlibrary.util

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
import com.techjays.chatlibrary.model.common.Option

/**
 * Created by Sharon on 10.7.20.
 **/

class DialogOptionAdapter(
    var mContext: Context,
    var mData: ArrayList<Option>,
    var mCallback: Callback
) : RecyclerView.Adapter<DialogOptionAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.inflate_dialog_option, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {

        val option = mData[position]
        holder.mTitle.text = option.mName
        holder.mMain.setOnClickListener {
            mCallback.select(position, option)
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    open class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mImage: ImageView = view.findViewById(R.id.image)
        var mTitle: TextView = view.findViewById(R.id.title)
        var mMain: LinearLayout = view.findViewById(R.id.main_layout)
    }

    interface Callback {
        fun select(position: Int, option: Option)
    }
}
