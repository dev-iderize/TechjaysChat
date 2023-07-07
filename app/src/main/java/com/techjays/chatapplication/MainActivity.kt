package com.techjays.chatapplication

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.techjays.chatlibrary.ChatLibrary
import com.techjays.chatlibrary.chatlist.LibChatListFragment

class MainActivity : AppCompatActivity() {
    private lateinit var mButton2: Button
    var UserToken = "25ad4b2fd401b553b9c3f1be98fb2f4c5f9c790f"
    var chatToken = ""
    val baseUrl = "https://stg-api.shieldup.ai/api/portal/"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ChatLibrary.instance.baseUrl = baseUrl
        initView()
    }


    private fun initView() {
        mButton2 = findViewById(R.id.nav_fragment)
        mButton2.setOnClickListener {
            val newFragment = LibChatListFragment()
            val bundle = Bundle()
            bundle.putString("base_url", baseUrl)
            bundle.putString("socket_url", "wss://stg-api.shieldup.ai/wss")
            bundle.putString("auth_token", UserToken)
            bundle.putString("phone_number", "6379425860")
            bundle.putInt("user_id", 247)

            newFragment.arguments = bundle

            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.root_container, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}