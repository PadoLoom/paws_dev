package hongik.capstone.bpsk

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.ByteArrayOutputStream


class nAudioCapture {
    private var audioRecord: AudioRecord? = null
    private var bufferSize = 0

    @SuppressLint("MissingPermission")
    fun start() {
        bufferSize = AudioRecord.getMinBufferSize(
            48000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            48000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioRecord!!.startRecording()
    }

    fun captureAudio(durationMillis: Int): ByteArray {
        val data = ByteArray(bufferSize)
        val out = ByteArrayOutputStream()
        val bytesToCapture = (durationMillis / 1000.0 * 48000 * 2).toInt() // 16비트(2바이트) 샘플

        while (out.size() < bytesToCapture) {
            val numBytesRead = audioRecord!!.read(data, 0, data.size)
            if (numBytesRead > 0) {
                out.write(data, 0, numBytesRead)
            }
        }

        return out.toByteArray()
    }

    fun stop() {
        audioRecord!!.stop()
        audioRecord!!.release()
        audioRecord = null
    }
}