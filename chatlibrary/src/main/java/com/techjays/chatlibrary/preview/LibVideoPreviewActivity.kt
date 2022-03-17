package com.techjays.chatlibrary.preview

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.techjays.chatlibrary.R
import kotlinx.android.synthetic.main.activity_lib_video_preview.*

class LibVideoPreviewActivity : AppCompatActivity() {

    companion object{
        var mCallback: Callback? = null

        fun newInstance(callback: Callback?): LibVideoPreviewActivity {
            mCallback = callback
            return LibVideoPreviewActivity()
        }
    }

    lateinit var mBack: ImageView
    var mPlayer: ExoPlayer? = null
    lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lib_video_preview)
        init()
    }

    private fun init() {
        val data = intent
        val mUrl = data.getStringExtra("url_data")
        val isUpload = data.getBooleanExtra("preview", false)

        mBack = findViewById(R.id.libImgBack)
        playerView = findViewById(R.id.videoView)

        mBack.setOnClickListener {
            mPlayer!!.release()
            mPlayer!!.stop()
            mPlayer = null
            onBackPressed()
        }

        if (isUpload) {
            libuploadButton.visibility = View.VISIBLE
        } else {
            libuploadButton.visibility = View.GONE
        }

        libuploadButton.setOnClickListener {
            mPlayer!!.release()
            mPlayer!!.stop()
            mPlayer = null
            mCallback?.videoPreviewCallback(mUrl!!)
            onBackPressed()

        }

        mPlayer = ExoPlayer.Builder(this).build()
        playerView.player = mPlayer
        mPlayer!!.setMediaSource(buildMediaSource(mUrl.toString()))
        val mediaItem: MediaItem = MediaItem.fromUri(mUrl!!.toUri())
        mPlayer!!.setMediaItem(mediaItem)
        mPlayer!!.playWhenReady = true
        mPlayer!!.prepare()
        mPlayer!!.play()
    }

    private fun buildMediaSource(mVideo: String): MediaSource {
        // Create a data source factory.
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(mVideo))
    }

    interface Callback {
        fun videoPreviewCallback(mUrl: String)
    }
}