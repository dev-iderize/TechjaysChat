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
            intent.putExtra("socket_url", "ws://3.19.93.161:8765")
            intent.putExtra(
                "chat_token",
                "gAAAAABhTeNwMK-408SXqM8BOo-JsuaWklyQk26ZZMbXtDoeGFzQZHRlkrv-bC1WiDpXKYlG_ftRXlMfNzs-zrzGXwaZbRldgSSCOuPBLrXN71flNYOimZUGjlSiy3aVRqg3ZDqRqGQ93tJffdg_nwDXXS5AFwnENNuXlfzhztxvUPo6vrwBjicqRFPfeNvOl1TA2s0G7uLITxKRYbjWtVbPKkP5jfHpoYNlx1-waoS9KnHc-HPNR_NuX9LEsx0Rghhrl77I97w6azVLqI0SZmxFmHGc9-SlcKS76X3lADcTkMpOerXti3E="
            )
            intent.putExtra("auth_token", "1d0e5734f76ad754333a3b297442a4b1f38eb60e")
            startActivity(intent)
        }

        mButton2 = findViewById(R.id.nav_fragment)
        mButton2.setOnClickListener {

            val newFragment = LibChatListFragment()

            val bundle = Bundle()
            bundle.putString("base_url", "https://sprint.myvidhire.com/api/v1/")
            bundle.putString("socket_url", "ws://3.19.93.161:8765")
            bundle.putString(
                "chat_token",
                "gAAAAABhTeNwMK-408SXqM8BOo-JsuaWklyQk26ZZMbXtDoeGFzQZHRlkrv-bC1WiDpXKYlG_ftRXlMfNzs-zrzGXwaZbRldgSSCOuPBLrXN71flNYOimZUGjlSiy3aVRqg3ZDqRqGQ93tJffdg_nwDXXS5AFwnENNuXlfzhztxvUPo6vrwBjicqRFPfeNvOl1TA2s0G7uLITxKRYbjWtVbPKkP5jfHpoYNlx1-waoS9KnHc-HPNR_NuX9LEsx0Rghhrl77I97w6azVLqI0SZmxFmHGc9-SlcKS76X3lADcTkMpOerXti3E="
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