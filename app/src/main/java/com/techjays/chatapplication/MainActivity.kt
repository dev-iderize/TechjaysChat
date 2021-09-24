package com.techjays.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.FragmentTransaction
import com.techjays.chatlibrary.chatlist.LibChatListActivity
import com.techjays.chatlibrary.chatlist.LibChatListFragment

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
            val intent = Intent(this@MainActivity, LibChatListActivity::class.java)
            intent.putExtra("base_url", "https://sprint.myvidhire.com/api/v1/")
            intent.putExtra(
                "chat_token",
                "gAAAAABhTcNgST12W47jH_ry5xIk15L0iiTgIKzYA1odECOeaRrxGVgxuB1a8ZRntao5UGUyfgSmI8LlVbhUEqN4K8M_yh5LGGjLEO5WwBWDGtLRK4OZT0rPS29UIiarAw_LIAL0eynum-wE562B9q7605qYZY3IDQ=="
            )
            intent.putExtra("auth_token", "1d0e5734f76ad754333a3b297442a4b1f38eb60e")
            startActivity(intent)
        }

        mButton2 = findViewById(R.id.nav_fragment)
        mButton2.setOnClickListener {

            val newFragment = LibChatListFragment()

            val bundle = Bundle()
            bundle.putString("base_url", "https://sprint.myvidhire.com/api/v1/")
            bundle.putString(
                "chat_token",
                "gAAAAABhTcNgST12W47jH_ry5xIk15L0iiTgIKzYA1odECOeaRrxGVgxuB1a8ZRntao5UGUyfgSmI8LlVbhUEqN4K8M_yh5LGGjLEO5WwBWDGtLRK4OZT0rPS29UIiarAw_LIAL0eynum-wE562B9q7605qYZY3IDQ=="
            )
            bundle.putString("auth_token", "1d0e5734f76ad754333a3b297442a4b1f38eb60e")
            newFragment.arguments = bundle

            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.root_container, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}