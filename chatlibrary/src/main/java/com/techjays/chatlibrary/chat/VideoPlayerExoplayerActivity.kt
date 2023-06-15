package com.techjays.chatlibrary.chat

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.databinding.ActivityVideoPlayerExoplayerBinding


class VideoPlayerExoplayerActivity : AppCompatActivity() {
    lateinit var binding: ActivityVideoPlayerExoplayerBinding
    var player: ExoPlayer? = null
    private val isPlaying get() = player?.playWhenReady ?: false
    private lateinit var playerView: StyledPlayerView
    private var playbackPosition: Long = 0
    private var currentWindow: Int = 0
    private var playWhenReady = true

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this,
                R.layout.activity_video_player_exoplayer
            ) as ActivityVideoPlayerExoplayerBinding
        playerView = binding.playerView
        binding.activity = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }


    }

    override fun onResume() {
        super.onResume()
        if (!isPlaying) {
            initializePlayerWithPlaybackPosition()
        }
    }

    private fun initializePlayerWithPlaybackPosition() {
        val videoUri = intent.getStringExtra("videoUri")
        if (videoUri.isNullOrEmpty()) {
            return
        }

        player = ExoPlayer.Builder(this).build()

        val mediaItem = MediaItem.Builder()
            .setUri(videoUri)
            .setMimeType(MimeTypes.APPLICATION_MP4)
            .build()

        val mediaSource = ProgressiveMediaSource.Factory(DefaultDataSource.Factory(this))
            .createMediaSource(mediaItem)

        player!!.apply {
            setMediaSource(mediaSource)
            playWhenReady = playWhenReady
            seekTo(currentWindow, playbackPosition)
            prepare()
        }.also {
            playerView.player = it
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            pausePlayer()
        }
    }

    private fun startPlayer() {
        if (!isPlaying) {
            player!!.playWhenReady = true
        }
    }

    private fun pausePlayer() {
        if (isPlaying) {
            player!!.playWhenReady = false
            playbackPosition = player!!.currentPosition
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("playbackPosition", playbackPosition)
        outState.putBoolean("playWhenReady", playWhenReady)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        playbackPosition = savedInstanceState.getLong("playbackPosition", 0)
        playWhenReady = savedInstanceState.getBoolean("playWhenReady", true)
    }

    private fun releasePlayer() {
        player!!.release()
    }
}