package com.techjays.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.techjays.chatlibrary.ChatListActivity
import com.techjays.chatlibrary.ChatListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mButton: Button
    private lateinit var mButton2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        mButton = findViewById(R.id.nav_activity)
        mButton.setOnClickListener {
            val intent = Intent(this@MainActivity, ChatListActivity::class.java)
            intent.putExtra("base_url", "https://sprint.myvidhire.com/api/v1/")
            intent.putExtra(
                "chat_token",
                "gAAAAABhSWvSuYM-mQooy08PdAcCOiz54P9dH_bdLHXX8w0uPAfURFMY9KbBm4OGmYvisqvLtpM1D7d0z1lwfgaod6cDnCjDQc5is1T5ezxsyTRCWQDMrSuya4WzHZz1QGxIPq5Utzs7iFnnYGcrMEACyy6ZBLuh8g=="
            )
            intent.putExtra("auth_token", "1d0e5734f76ad754333a3b297442a4b1f38eb60e")
            startActivity(intent)
        }

        mButton2 = findViewById(R.id.nav_fragment)
        mButton2.setOnClickListener {

            val newFragment = ChatListFragment.newInstance(
                "https://sprint.myvidhire.com/api/v1/",
                "gAAAAABhSWvSuYM-mQooy08PdAcCOiz54P9dH_bdLHXX8w0uPAfURFMY9KbBm4OGmYvisqvLtpM1D7d0z1lwfgaod6cDnCjDQc5is1T5ezxsyTRCWQDMrSuya4WzHZz1QGxIPq5Utzs7iFnnYGcrMEACyy6ZBLuh8g==",
                "1d0e5734f76ad754333a3b297442a4b1f38eb60e"
            )

            val transaction: FragmentTransaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.root_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}