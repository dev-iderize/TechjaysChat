package com.techjays.chatlibrary.preview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.util.Utility
import kotlinx.android.synthetic.main.activity_lib_image_preview.*

class LibImagePreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lib_image_preview)

        val data = intent
        val mUrl = data.getStringExtra("url_data")

        Utility.loadUserImage(
            mUrl, photo_view,
            this
        )

        libImgBack.setOnClickListener {
            onBackPressed()
        }
    }
}