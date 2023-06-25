import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioRecorder(
    private val context: Context,
    private val mAudioRecorderCallback: AudioRecorderCallBack,
    private val fileProviderAuthority: String // Add this parameter for FileProvider authority
) {
    private val TAG = context.packageName + ".AudioRecorder"
    private val AUDIO_FILE_PREFIX = "Recording_"
    private val AUDIO_FILE_SUFFIX = ".mp3"

    private var mediaRecorder: MediaRecorder? = null
    private var currentFilePath: String? = null
    private var isRecording = false

    fun startRecording() {
        try {
            if (isRecording) {
                Log.d(TAG, "Recording already in progress.")
                return
            }
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "$AUDIO_FILE_PREFIX$timestamp$AUDIO_FILE_SUFFIX"

            val audioFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)
            currentFilePath = audioFile.absolutePath
            Log.e("file", "$currentFilePath")

            mediaRecorder = MediaRecorder()
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder?.setOutputFile(currentFilePath)
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            isRecording = true
            Log.d(TAG, "Recording started.")
        } catch (e: IOException) {
            Log.e(TAG, "Failed to start recording: ${e.message}")
        }
    }

    private fun getFileUri(filePath: String?): Uri? {
        return FileProvider.getUriForFile(
            context,
            fileProviderAuthority,
            File(filePath!!)
        )
    }

    fun stopRecording() {
        val fileUri = getFileUri(currentFilePath)

        try {
            mediaRecorder?.apply {
                setOnErrorListener(null)
                setOnInfoListener(null)
                setPreviewDisplay(null)
                stop()
                release()
                mediaRecorder = null
                isRecording = false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop recording: ${e.message}")
        }

        mAudioRecorderCallback.onAudioRecordingCompleted(fileUri)

        Log.d(TAG, "Recording stopped. File saved to: $currentFilePath")
        if (!isRecording) {
            Log.d(TAG, "No recording in progress.")
            return
        }
    }


    interface AudioRecorderCallBack {
        fun onAudioRecordingCancelled(path: String?)
        fun onAudioRecordingCompleted(uri: Uri?)
    }
}
