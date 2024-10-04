package hongik.capstone.bpsk;

import android.annotation.SuppressLint
import android.media.AudioRecord
import android.util.Log
import com.example.paws_dev.Decoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@SuppressLint("MissingPermission")
class nBPSK {
    private val SAMPLE_RATE = 48000 // 샘플레이트 48KHz
    private val CARRIER_FREQUENCY = 19000 // 반송주파수 19KHz
    private val BITS_PER_SECOND = 64 // 수신률 초당 128비트
    private val BIT_DURATION = 1.0 / BITS_PER_SECOND

    private val TARGET_PATTERN = "01000001"

    private var audioRecord: AudioRecord? = null
    private var isReceiving = false
    private val freqDetector = nFreqDetector()
    private val audioCapture = nAudioCapture()



    suspend fun startReceiving(pattern: ShortArray): Boolean = withContext(Dispatchers.IO) {
        isReceiving = true
        audioCapture.start()

        var detected = false
        Log.d("nBPSK", "startReceiving")

        while (isReceiving) {
            val read = audioCapture.captureAudio(300)
            val magnitude = freqDetector.detectFrequencyMagnitude(read)
            if(magnitude >= 0.8){
                val decodedBits = Decoder.decodeAudioData(read,
                    CARRIER_FREQUENCY.toDouble(), SAMPLE_RATE, BIT_DURATION)
                val bitString = convertBitsToString(decodedBits)
                Log.d("nBPSK", "DecodedBits : $bitString")
                if (bitString.contains(TARGET_PATTERN)) {
                    Log.d("nBPSK", "Pattern Detected!")
                    stopReceiving()
                    detected = true
                    break
                }
            }

        }

        detected
    }

    fun stopReceiving() {
        isReceiving = false
        audioRecord?.stop()
    }

    private fun convertBitsToString(bits: IntArray): String {
        val sb = StringBuilder()
        for (bit in bits) {
            sb.append(bit)
        }
        return sb.toString()
    }


}