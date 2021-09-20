package com.techjays.chatlibrary

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class ChatActivity : AppCompatActivity() {
    lateinit var Text: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        Text = findViewById(R.id.message)

        try {
            val data = intent
            val name = data.getStringExtra("message").toString()
            Text.text = name
        } catch (e: Exception) {
            Log.d("ex", e.toString())
        }
    }
}