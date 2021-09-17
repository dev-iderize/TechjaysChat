package com.techjays.chatapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.techjays.chatlibrary.ChatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val homeFragment = ChatActivity()

        val bundle = Bundle()
        val myMessage = "Message from the root app"
        bundle.putString("message", myMessage)
        homeFragment.setArguments(bundle)

        if(savedInstanceState == null) { // initial transaction should be wrapped like this
            supportFragmentManager.beginTransaction()
                .replace(R.id.root_container, homeFragment)
                .commitAllowingStateLoss()
        }

    }
}