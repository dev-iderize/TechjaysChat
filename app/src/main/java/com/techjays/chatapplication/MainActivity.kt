package com.techjays.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.FragmentTransaction
import com.techjays.chatlibrary.LibChatListActivity
import com.techjays.chatlibrary.LibChatListFragment

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
                "gAAAAABhSyKfrKoBKtN_SDmfp_hHmMdxU3igV3dc99h9RcVJaVHC57N2VhYNptZmzyTV8CUh09mDJvPcr4cWVekeRBwC9o9gQSFIV2ZB9NeURjIgIRvkEfZwwIF3fEpmq2a8rs2rSIRCWzCIX1g6YJOlbA5Gsu_UbQ=="
            )
            intent.putExtra("auth_token", "5cee957a8fe5b72393d2511818c33eef5fd18d1c")
            startActivity(intent)
        }

        mButton2 = findViewById(R.id.nav_fragment)
        mButton2.setOnClickListener {

            val newFragment = LibChatListFragment()

            val bundle = Bundle()
            bundle.putString("base_url", "https://sprint.myvidhire.com/api/v1/")
            bundle.putString(
                "chat_token",
                "gAAAAABhSxSGLih9x9FtS2q2LjW4IsGn_oFDpGIyrsOY0qygwPRb-h0R6BSxtZ3AT0qmVHzHVZ2X--z0R9k1BRK5OYdvRnhYDH0tmK0CT2w9rNBKS6-b38MWvTrvPjzlkB8IxuJq7cMPyVF3KuoEiLvBfYs6IpuJzg=="
            )
            bundle.putString("auth_token", "5cee957a8fe5b72393d2511818c33eef5fd18d1c")
            newFragment.arguments = bundle

            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.root_container, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}