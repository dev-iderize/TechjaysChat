package com.techjays.chatlibrary.preview

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.util.Utility

class LibImagePreviewActivity : AppCompatActivity() {

    lateinit var mPhoto: ImageView
    lateinit var mBack:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lib_image_preview)
        val data = intent
        var mUrl = data.getStringExtra("url_data")

        mPhoto = findViewById(R.id.photo_view)
        mBack = findViewById(R.id.libImgBack)

        Utility.loadUserImage(
            mUrl, mPhoto,
            this
        )

        mBack.setOnClickListener {
            onBackPressed()
        }
    }
}