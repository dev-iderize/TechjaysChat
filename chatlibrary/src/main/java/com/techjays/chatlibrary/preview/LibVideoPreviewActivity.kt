package com.techjays.chatlibrary.preview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.net.toUri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.techjays.chatlibrary.R

class LibVideoPreviewActivity : AppCompatActivity() {

    lateinit var mBack: ImageView
    var mPlayer: ExoPlayer? = null
    lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lib_video_preview)
        init()
    }

    private fun init(){
        val data = intent
        val mUrl = data.getStringExtra("url_data")
        mBack = findViewById(R.id.libImgBack)
        playerView = findViewById(R.id.videoView)

        mBack.setOnClickListener {
            mPlayer!!.release()
            mPlayer!!.stop()
            mPlayer = null
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
}