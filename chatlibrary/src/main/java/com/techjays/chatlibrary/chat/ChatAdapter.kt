package com.techjays.chatlibrary.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.techjays.chatlibrary.R
import com.techjays.chatlibrary.databinding.InflateAudioTypeBinding
import com.techjays.chatlibrary.databinding.InflateChatInfoMessageBinding
import com.techjays.chatlibrary.databinding.InflateChatYBinding
import com.techjays.chatlibrary.databinding.InflateVideoTypeBinding
import com.techjays.chatlibrary.helpers.AspectRatioCallback
import com.techjays.chatlibrary.helpers.ImageUtils
import com.techjays.chatlibrary.model.Chat

class ChatAdapter(
    val context: LibChatActivity, private val messages: ArrayList<Chat.ChatData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_TEXT = 0
        private const val VIEW_TYPE_VIDEO = 1
        private const val VIEW_TYPE_AUDIO = 2
        private const val VIEW_TYPE_INFO_MESSAGE = 3
    }

    private var currentAudioUrl: String? = null
    private var currentPlayingPosition: Int = -1
    private var previousPlayingPosition: Int = -1
    private val videoImageUrls = mutableMapOf<Int, String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: RecyclerView.ViewHolder = when (viewType) {
            VIEW_TYPE_TEXT -> {
                val textItemView = InflateChatYBinding.inflate(inflater, parent, false)
                TextViewHolder(textItemView)
            }

            VIEW_TYPE_VIDEO -> {
                val videoItemView = InflateVideoTypeBinding.inflate(inflater, parent, false)
                VideoViewHolder(videoItemView)
            }

            VIEW_TYPE_AUDIO -> {
                val audioItemView = InflateAudioTypeBinding.inflate(inflater, parent, false)
                AudioViewHolder(audioItemView)
            }

            VIEW_TYPE_INFO_MESSAGE -> {
                val infoItemView = InflateChatInfoMessageBinding.inflate(inflater, parent, false)
                InfoViewHolder(infoItemView)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }

        return binding
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        when (holder.itemViewType) {
            VIEW_TYPE_TEXT -> {
                val textHolder = holder as TextViewHolder
                val binding = textHolder.binding
                binding.message = message
                val maxCharLength = 150
                binding.isSentByMyself = !message.isSentByMyself

                val fullMessage = message.mMessage
                if (message.mMessage.length > maxCharLength) {
                    val shortenedMessage = message.mMessage.substring(0, maxCharLength)
                    binding.textRecipientMessage.text = "$shortenedMessage..."
                    binding.textReadMore.visibility = View.VISIBLE
                    binding.textReadMore.setOnClickListener {
                        binding.textRecipientMessage.text = fullMessage
                        binding.textReadMore.visibility = View.GONE
                    }
                } else {
                    binding.textRecipientMessage.text = message.mMessage
                    binding.textReadMore.visibility = View.GONE
                }
                binding.executePendingBindings()
            }

            VIEW_TYPE_INFO_MESSAGE -> {
                val infoHolder = holder as InfoViewHolder
                val binding = infoHolder.binding
                binding.message = message
                val mNotificationType = when {
                    message.mMessage.contains("turned on their Shield") -> "SHIELD_ON"

                    message.mMessage.contains("turned off their Shield") -> "SHIELD_OFF"

                    message.mMessage.contains("turned off their SOS") -> "SOS_OFF"

                    message.mMessage.contains("triggered SOS") -> "SOS_ON"
                    else -> ""
                }
                binding.messageType = mNotificationType
                binding.executePendingBindings()

            }

            VIEW_TYPE_VIDEO -> {
                val videoHolder = holder as VideoViewHolder
                val binding = videoHolder.binding
                binding.message = message
                binding.isSentByMyself = !message.isSentByMyself

                val isVideo = message.mMessageType == "video"
                binding.isVideo = isVideo
                videoImageUrls[videoHolder.bindingAdapterPosition] = message.mMessage

                ImageUtils.getAspectRatioFromUrl(message.mMessage, object : AspectRatioCallback {
                    override fun onAspectRatioLoaded(aspectRatio: Float, imageUrl: String) {
                        if (imageUrl == videoImageUrls[videoHolder.bindingAdapterPosition]) {
                            binding.ratio = aspectRatio
                            binding.image = message.mMessage
                        }
                    }
                })


                binding.videoLayout.setOnClickListener {
                    if (isVideo) {
                        val i = Intent(context, VideoPlayerExoplayerActivity::class.java)
                        i.putExtra(
                            "videoUri",
                            message.mMessage
                        )
                        context.startActivity(i)
                    } else {
                        context.showImageViewer(message.mMessage)
                    }
                }
                binding.executePendingBindings()
            }


            else -> {
                val audioHolder = holder as AudioViewHolder
                val binding = audioHolder.binding
                binding.message = message
                binding.isSentByMyself = message.isSentByMyself
                binding.executePendingBindings()
            }
        }
    }


    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return when (message.mMessageType) {
            "audio" -> VIEW_TYPE_AUDIO
            "video", "image" -> VIEW_TYPE_VIDEO
            "notification" -> VIEW_TYPE_INFO_MESSAGE
            else -> VIEW_TYPE_TEXT
        }
    }

    override fun getItemId(position: Int): Long {
        return messages.size.toLong()
    }

    inner class TextViewHolder(val binding: InflateChatYBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Text type view holder
    }

    inner class InfoViewHolder(val binding: InflateChatInfoMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


    inner class VideoViewHolder(val binding: InflateVideoTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    inner class AudioViewHolder(val binding: InflateAudioTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isAudioPlaying = false
        private var pausedPosition: Int = 0
        private var audioProgress: Int = 0
        private val handler: Handler = Handler(Looper.getMainLooper())

        init {
            val playPauseButton = binding.playPauseButton
            val seekBar: ProgressBar = binding.seekBar

            playPauseButton.setOnClickListener {
                binding.isLoading = true
                val message = messages[bindingAdapterPosition]
                val audioUrl = message.mMessage
                stopOtherMediaPlayers()

                if (isAudioPlaying && currentAudioUrl == audioUrl) {
                    pauseAudio()
                    playPauseButton.setImageResource(R.drawable.ic_play_button)
                } else {
                    if (currentAudioUrl == audioUrl) {
                        resumeAudio()
                    } else {
                        playAudio(audioUrl)
                    }
                    playPauseButton.setImageResource(R.drawable.ic_pause_icon)
                }
                context.mediaPlayer?.let { player ->
                    seekBar.max = player.duration
                    updateSeekBar(seekBar)
                }
            }
            binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                    if (fromUser) {
                        if (currentPlayingPosition == bindingAdapterPosition) {
                            context.mediaPlayer?.seekTo(progress)
                            audioProgress = progress
                            binding.percentage.text = audioProgress.toString()
                        }

                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        private fun playAudio(audioUrl: String) {
            context.mediaPlayer?.reset()
            context.mediaPlayer = MediaPlayer().apply {
                setDataSource(audioUrl)
                prepare()
                start()
                currentAudioUrl = audioUrl
                isAudioPlaying = true
            }
            pausedPosition = 0
            handler.postDelayed({
                updateSeekBar(binding.progressBar)
            }, 1000)
            currentPlayingPosition = bindingAdapterPosition
            binding.isLoading = false
            context.mediaPlayer?.setOnCompletionListener {
                isAudioPlaying = false
                pausedPosition = 0
                currentPlayingPosition = -1
                currentAudioUrl = null
                binding.playPauseButton.setImageResource(R.drawable.ic_play_button)
                binding.progressBar.progress = 0
                binding.seekBar.progress = 0
            }
            if (currentPlayingPosition == bindingAdapterPosition) binding.seekBar.visibility =
                View.VISIBLE
        }


        private fun pauseAudio() {
            binding.isLoading = false
            context.mediaPlayer?.pause()
            pausedPosition = context.mediaPlayer?.currentPosition ?: 0
            isAudioPlaying = false
            audioProgress = pausedPosition
            binding.percentage.text = audioProgress.toString()
            previousPlayingPosition = currentPlayingPosition
            currentPlayingPosition = -1
            //invisible
            binding.seekBar.visibility = View.VISIBLE
        }

        private fun resumeAudio() {
            binding.isLoading = false
            context.mediaPlayer?.seekTo(pausedPosition)
            context.mediaPlayer?.start()
            isAudioPlaying = true
            audioProgress = pausedPosition
            currentPlayingPosition = bindingAdapterPosition
            if (currentPlayingPosition == bindingAdapterPosition) binding.seekBar.visibility =
                View.VISIBLE
        }

        private fun stopOtherMediaPlayers() {
            for (i in 0 until messages.size) {
                if (i != bindingAdapterPosition) {
                    val viewHolder =
                        context.binding.chatRecyclerView.findViewHolderForAdapterPosition(i)
                    if (viewHolder is AudioViewHolder) {
                        viewHolder.pauseAudio()
                        viewHolder.binding.playPauseButton.setImageResource(R.drawable.ic_play_button)
                        viewHolder.binding.progressBar.progress = 0

                        //invisible
                        viewHolder.binding.seekBar.visibility = View.VISIBLE
                    }
                }
            }
        }

        private fun updateSeekBar(seekBar: ProgressBar) {
            if (bindingAdapterPosition == currentPlayingPosition) {
                context.mediaPlayer?.let { player ->
                    audioProgress = player.currentPosition
                    seekBar.progress = audioProgress
                    binding.percentage.text = audioProgress.toString()
                    if (player.isPlaying) {
                        handler.postDelayed({
                            updateSeekBar(seekBar)
                        }, 1000)
                    }
                }
            } else {
                seekBar.progress = 0
            }
        }


    }
}