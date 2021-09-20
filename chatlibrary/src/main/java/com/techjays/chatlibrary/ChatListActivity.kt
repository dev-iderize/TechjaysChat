package com.techjays.chatlibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class ChatListActivity : AppCompatActivity() {
    lateinit var Text: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        Text = findViewById(R.id.textViewActivity)

        try {
            val data = intent
            val name = data.getStringExtra("name").toString()
            Text.text = name
        } catch (e: Exception) {
            Log.d("ex", e.toString())
            throw  e
        }
    }
}