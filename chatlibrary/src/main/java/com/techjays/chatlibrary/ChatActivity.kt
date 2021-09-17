package com.techjays.chatlibrary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView


class ChatActivity : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_chat, container, false)
        val bundle = this.arguments
        val myValue = bundle!!.getString("message")
        val text: TextView = view.findViewById(R.id.text)
        text.text = myValue
        return view;
    }
}