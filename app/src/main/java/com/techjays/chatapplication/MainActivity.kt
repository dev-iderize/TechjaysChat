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
import com.techjays.chatlibrary.api.LibAppServices
import com.techjays.chatlibrary.api.Response
import com.techjays.chatlibrary.api.ResponseListener
import com.techjays.chatlibrary.chatlist.LibChatListActivity
import com.techjays.chatlibrary.chatlist.LibChatListFragment
import com.techjays.chatlibrary.model.User
import com.techjays.chatlibrary.util.AppDialogs
import com.techjays.chatlibrary.util.Utility

class MainActivity : AppCompatActivity() {
    private lateinit var mButton2: Button
    var UserToken = "c3901780510ded844ac0354d922374ce22a422fc"
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
            bundle.putInt("user_id", 215)

            newFragment.arguments = bundle

            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.root_container, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}