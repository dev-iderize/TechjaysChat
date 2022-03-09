package com.techjays.chatapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.gson.Gson
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
                    Utility.statusBarColor(
                        window,
                        this,
                        com.techjays.chatlibrary.R.color.status_pink
                    )
                else
                    Utility.statusBarColor(window, this, com.techjays.chatlibrary.R.color.dark_grey)
            }
        }
    }


    private fun initView() {
        mButton = findViewById(R.id.nav_activity)
        mButton.setOnClickListener {
            val intent = Intent(this@MainActivity, LibChatListActivity::class.java)

            val chatUserData = ChatUserModel()
            chatUserData.mChatToken =
                "gAAAAABiJ6vYnPU3cuVO8zU01Fi1e8Z8_1gHT-DtAF6Z5JNZT3DVAWZzQmG9NNh9ojg_YmZz3TL1gvxJVS5Rm392QY-Bx3ciy_jmPjpWQ1s3Wq92eq24lrnfTzVtTBdstXeVbPAaRHnfvyB3EAk-V1R3F2fJCdifVj-I7m078zkhrf9Zs9q_AMB5FY6A3BdhCd2MUIIDAYnzL9zPE4IC8hh87OwYWXg22C0o5DoOT3SsVeAz9yrc8EFwLbCNxP287KYfqBr0DteHIJt7uMGIc7--p0N2_xgViiZNOHt2Aa4dGjRQh6YNcwc="
            chatUserData.mAuthToken = "1c40b92d06bc7ec7744b60bd04e86ad52332264d"
            chatUserData.mBaseUrl = "https://dev-myvidrivals.myvidhire.com/api/v1/"
            chatUserData.mSocketUrl = "ws://18.217.53.197:8765/"
            chatUserData.mIsImage = true
            chatUserData.mIsVideo = true
            chatUserData.mItemId = "2895"
            chatUserData.mSenderUserId = "212"
            chatUserData.mSenderFullName = "Anugraha tv"
            chatUserData.mSenderProfilePicUrl =
                "https://d1r0dpdlaij12c.cloudfront.net/media/public/profile/images/profile212.png"
            /*if (mUser.mUser!!.mUserId == data.mChallengeUser!!.mUserId){
                chatUserData.mReceiverUserId = data.mAcceptedUser!!.mUserId.toString()
                chatUserData.mReceiverFullName = data.mAcceptedUser!!.mFullName
                chatUserData.mReceiverProfilePicUrl = data.mAcceptedUser!!.mProfilePic
            }else{
                chatUserData.mReceiverUserId = data.mChallengeUser!!.mUserId.toString()
                chatUserData.mReceiverFullName = data.mChallengeUser!!.mFullName
                chatUserData.mReceiverProfilePicUrl = data.mChallengeUser!!.mProfilePic
            }*/
            chatUserData.mReceiverUserId = "245"
            chatUserData.mReceiverFullName = "Srinath"
            chatUserData.mReceiverProfilePicUrl = "https://www.srinathdev.me/img/night.png"
            chatUserData.mHeaderColor = "#443567"

            /*intent.putExtra("base_url", "https://dev.myfayvit.com/api/v1/")
            intent.putExtra("socket_url", "ws://18.217.53.197:8765/")
            intent.putExtra(
                "chat_token",
                "gAAAAABiJu4OMmSnv9C7sfC8K65jNBYunrWpRdXaO-JPrL-Pw-58jNqnriV0Ov9HpvQOTyRS6G17l9-fN9c-5kOvWr2tl38_deLuv2bzMQ67nK0J91gHCE2lsioqJr1fRv_qctbq5YE0sQEWMkZnMKdbXnk9SHZA2kFnReY1kfIdwjORQEKZBG4=")
            intent.putExtra("auth_token", "d83c7b7472b2a2a57d5408bb4cd7e5d4b76c8039")
            intent.putExtra("color", "#443567")*/

            intent.putExtra("chat_data", Gson().toJson(chatUserData))

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

/**fayvit username :Dont Follow
 *             intent.putExtra("base_url", "https://dev.myfayvit.com/api/v1/")
intent.putExtra("socket_url", "ws://3.16.6.93:8765")
intent.putExtra(
"chat_token",
"gAAAAABhUpQJRce0WvkDNeS0HMmPA5mNQrIcXHliSpCfS-SZajSb_gsL6mnGjSXyCVwCLU8-9RrwULyjER9csrMMtS3IZf4t6iKWkB1ufurDVfIreP19Zuv-CaFgLnYG5G0ktwgh8cuklLStMtS38HtQ8SToCBd7L9Nv0jItjF63E0Lf3vWBVdMHPnVQzeeV5HBfDroLJvbh94gp7xjvT0jXqHszR83_DBABGKROILp7l_POzgEMsu_DzKHw8lxvbnMmhqFLt1wrrgZpLm-R_TRCbs0ZlvLm6Xgen_YvG_QGxgjLrIPy3HI="
)
intent.putExtra("auth_token", "d83c7b7472b2a2a57d5408bb4cd7e5d4b76c8039")
 */
