package com.techjays.chatlibrary.preview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.github.chrisbanes.photoview.PhotoView
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.util.Utility

class LibImagePreviewActivity : AppCompatActivity() {

    lateinit var mPhoto: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lib_image_preview)

        mPhoto = findViewById(R.id.photo_view)

        Utility.loadUserImage(
            "https://d1r0dpdlaij12c.cloudfront.net/media/public/files/file_111.jpg", mPhoto,
            this
        )
    }
}