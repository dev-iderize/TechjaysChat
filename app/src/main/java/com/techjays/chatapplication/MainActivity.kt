package com.techjays.chatapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.gson.Gson
import com.techjays.chatlibrary.ChatLibrary
import com.techjays.chatlibrary.chatlist.LibChatListActivity
import com.techjays.chatlibrary.chatlist.LibChatListFragment
import com.techjays.chatlibrary.util.AppDialogs
import com.techjays.chatlibrary.util.Utility

class MainActivity : AppCompatActivity() {

    private lateinit var mButton: Button
    private lateinit var mButton2: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }


    private fun initView() {
        mButton2 = findViewById(R.id.nav_fragment)
        mButton2.setOnClickListener {
            val newFragment = LibChatListFragment()

            val bundle = Bundle()
            bundle.putString("base_url", "https://stg-api.shieldup.ai/api/portal/")
            bundle.putString("socket_url", "wss://dev-api.shieldup.ai/wss")
            bundle.putString(
                "chat_token",
                "gAAAAABkipktRdeV4xHZ4t8zbidFF4TPnd8srqe4slyghtQNf5XYtlIIGtIZ3sjzAQ10oaUYAWmdJ5xDqfVUyursFyH1ErpXSeWKaLl5o59ArJ3fYWAh8QBjJ9gHcu7j7_j0Hmec-jC9tyuzv1xXCmsCJYqVsR-ekzi-wReqNcMm6jQSMba2WXmbgO1GUhtHhV8Ke1ASnN28yCJR-x1SqpW4teACugsuf4nRQeTVBIF8CiayxhvIxJo="
            )
            bundle.putString("auth_token", "de797cb4f058557a9b1b7d6c24186fa20f62b651")
            bundle.putInt("user_id", 218)
            newFragment.arguments = bundle

            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.root_container, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}