package com.techjays.chatlibrary.helpers

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AudioRecorder(
    private val context: Context,
    private val mAudioRecorderCallback: AudioRecorderCallBack
) {
    private val TAG = "com.techjays.chatlibrary.helpers.AudioRecorder"
    private val AUDIO_FILE_PREFIX = "Recording_"
    private val AUDIO_FILE_SUFFIX = ".3gp"

    private var mediaRecorder: MediaRecorder? = null
    private var currentFilePath: String? = null
    private var isRecording = false

    fun startRecording() {
        if (isRecording) {
            Log.d(TAG, "Recording already in progress.")
            return
        }

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "$AUDIO_FILE_PREFIX$timestamp$AUDIO_FILE_SUFFIX"

        val audioFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)
        currentFilePath = audioFile.absolutePath
        Log.e("file", "$currentFilePath")

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(currentFilePath)
            try {
                prepare()
                start()
                isRecording = true
                Log.d(TAG, "Recording started.")
            } catch (e: IOException) {
                Log.e(TAG, "Failed to start recording: ${e.message}")
            }
        }
    }

    fun stopRecording() {
        mAudioRecorderCallback.onAudioRecordingCompleted(currentFilePath)


        try {
            mediaRecorder?.apply {
                stop()
                release()
                mediaRecorder = null
                isRecording = false
            }
        } catch (_: Exception) {
        }

        Log.d(TAG, "Recording stopped. File saved to: $currentFilePath")
        if (!isRecording) {
            Log.d(TAG, "No recording in progress.")

            return
        }

    }

    interface AudioRecorderCallBack {
        fun onAudioRecordingCancelled(path: String?)
        fun onAudioRecordingCompleted(path: String?)
    }
}
