package com.techjays.chatapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.fragment.app.FragmentTransaction
import com.techjays.chatlibrary.ChatLibrary
import com.techjays.chatlibrary.chatlist.LibChatListActivity
import com.techjays.chatlibrary.chatlist.LibChatListFragment
import com.techjays.chatlibrary.util.Utility

class MainActivity : AppCompatActivity() {

    private lateinit var mButton: Button
    private lateinit var mButton2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        when (ChatLibrary.instance.mColor) {
            "#FF878E" -> {
                if (!Utility.isUsingNightModeResources(this))
                    Utility.statusBarColor(window, this, com.techjays.chatlibrary.R.color.status_pink)
                else
                    Utility.statusBarColor(window, this, com.techjays.chatlibrary.R.color.dark_grey)
            }
        }
    }

    private fun initView() {
        mButton = findViewById(R.id.nav_activity)
        mButton.setOnClickListener {
            val intent = Intent(this@MainActivity, LibChatListActivity::class.java)
            intent.putExtra("base_url", "https://sprint.myvidhire.com/api/v1/")
            intent.putExtra("socket_url", "ws://3.19.93.161:8765")
            intent.putExtra(
                "chat_token",
                "gAAAAABhZSW51UaxgpKuYVAbvP3a_r6zl_Emug25BLG2Xc2zT0PGx6_VW2Dw_niNrmP-D1_RX6ea46HkRH7roo_C2jRU4T3BWFOmBuI8C155UAnd3ATrrd5ej9bY_R1icmfF8jt7vqvJgmj6HjnmGTuxCII98WWnI2oalzmMflwul2dz8c1pipc0G0tAoe07iNx5GWgLKBbz6eiO5KQFUUwwhK0WvjXkCL7oE3KXN0kSlSHBjOwF40UWXZCQGsCkueOUlqDLtUsl7QWEOtZ1RdjV38edff4rQ0WYYPbaezmgy2YqddrbmYuPVSrj6h3VNfzim1yVV1H2"
            )
            intent.putExtra("auth_token", "5a501f8a779c544c2fdf18f28028bbc5a2e64fc3")
            intent.putExtra("color", "#FF878E")

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
            bundle.putString("color", "#FF878E")
            newFragment.arguments = bundle

            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.root_container, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}