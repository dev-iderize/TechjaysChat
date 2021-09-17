package com.techjays.chatapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.techjays.chatlibrary.ChatFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val homeFragment = ChatFragment.newInstance("test")
        if(savedInstanceState == null) { // initial transaction should be wrapped like this
            supportFragmentManager.beginTransaction()
                .replace(R.id.root_container, homeFragment)
                .commitAllowingStateLoss()
        }

    }
}