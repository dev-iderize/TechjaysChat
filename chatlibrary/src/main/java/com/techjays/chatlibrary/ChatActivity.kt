package com.techjays.chatlibrary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.techjays.chatlibrary.R as r;


class ChatActivity : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(r.layout.activity_chat, container, false)
        return view;
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = this.arguments
        val myValue = bundle!!.getString("message")
        val text: TextView = view.findViewById(r.id.text)
        text.text = myValue
    }
}