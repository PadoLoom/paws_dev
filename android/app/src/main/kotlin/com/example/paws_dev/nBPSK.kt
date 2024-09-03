package hongik.capstone.bpsk;

import android.annotation.SuppressLint
import android.media.AudioRecord
import android.util.Log
import com.example.paws_dev.Decoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.cos

@SuppressLint("MissingPermission")
class nBPSK {
    private val SAMPLE_RATE = 48000 // 샘플레이트 48KHz
    private val CARRIER_FREQUENCY = 19000 // 반송주파수 19KHz
    private val BIT_LENGTH = 16 // 16비트 길이
    private val BITS_PER_SECOND = 64 // 수신률 초당 128비트
    private val BIT_DURATION = 1.0 / BITS_PER_SECOND

    private val TARGET_PATTERN = "01000001"

    private var audioRecord: AudioRecord? = null
    private var isReceiving = false
    private val freqDetector = nFreqDetector()
    private val audioCapture = nAudioCapture()

//    init {
//        val bufferSize = AudioRecord.getMinBufferSize(
//                SAMPLE_RATE,
//                AudioFormat.CHANNEL_IN_MONO,
//                AudioFormat.ENCODING_PCM_16BIT
//        ) * 16
//
//        audioRecord = AudioRecord(
//                MediaRecorder.AudioSource.MIC,
//                SAMPLE_RATE,
//                AudioFormat.CHANNEL_IN_MONO,
//                AudioFormat.ENCODING_PCM_16BIT,
//                bufferSize
//        )
//    }

    suspend fun startReceiving(pattern: ShortArray): Boolean = withContext(Dispatchers.IO) {
        isReceiving = true
        audioCapture.start()

//        audioRecord?.startRecording()

//        val buffer = ByteArray(SAMPLE_RATE * 32 / BITS_PER_SECOND)
        var detected = false
        Log.d("nBPSK", "startReceiving")
//        Log.d("nBPSK", "Buffer Size : " + buffer.size)

        while (isReceiving) {
            val read = audioCapture.captureAudio(300)
            val magnitude = freqDetector.detectFrequencyMagnitude(read)
            if(magnitude >= 0.8){
//                    Log.d("nBPSK", "Over Threshold")
                val decodedBits = Decoder.decodeAudioData(read,
                    CARRIER_FREQUENCY.toDouble(), SAMPLE_RATE, BIT_DURATION)
//                    Log.d("nBPSK", "DecodedBits : $decodedBits")
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

    suspend fun startReceivingWithString(pattern: ShortArray): String? = withContext(Dispatchers.IO) {
        isReceiving = true
        audioCapture.start()

        var detectedBitString: String? = null
        Log.d("nBPSK", "startReceiving")

        while (isReceiving) {
            val read = audioCapture.captureAudio(300)
            val magnitude = freqDetector.detectFrequencyMagnitude(read)
            if (magnitude >= 0.8) {
                val decodedBits = Decoder.decodeAudioData(read,
                    CARRIER_FREQUENCY.toDouble(), SAMPLE_RATE, BIT_DURATION)
                val bitString = convertBitsToString(decodedBits)
                Log.d("nBPSK", "DecodedBits : $bitString")
                detectedBitString = bitString
                break

            }
        }

        detectedBitString
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

//    private fun processBuffer(buffer: ByteArray, pattern: ShortArray): Boolean {
//        // BPSK 복조 및 패턴 매칭
//        val shortBuffer = byteArrayToShortArray(buffer)
//        val demodulatedSignal = bpskDemodulate(shortBuffer)
//        val receivedBits = extractBits(demodulatedSignal)
//
//        val bitString = receivedBits.joinToString(separator = "") { it.toString() }
//        Log.d("nBPSK_process", "Decoded Bits: $bitString")
//
//
//        return matchPattern(receivedBits, pattern)
//    }

    private fun bpskDemodulate(buffer: ShortArray): DoubleArray {
        val n = buffer.size
        val demodulated = DoubleArray(n)

        for (i in 0 until n) {
            demodulated[i] = buffer[i] * cos(2 * PI * CARRIER_FREQUENCY * i / SAMPLE_RATE)
        }

        return demodulated
    }


    private fun extractBits(demodulatedSignal: DoubleArray): ShortArray {
        val numBits = demodulatedSignal.size / (SAMPLE_RATE / BITS_PER_SECOND)
        val bits = ShortArray(numBits)

        for (i in 0 until numBits) {
            var sum = 0.0
            for (j in 0 until SAMPLE_RATE / BITS_PER_SECOND) {
                sum += demodulatedSignal[i * (SAMPLE_RATE / BITS_PER_SECOND) + j]
            }
            bits[i] = if (sum > 0) 1 else 0
        }

        return bits
    }

//    private fun matchPattern(receivedBits: ShortArray, pattern: ShortArray): Boolean {
//        if (receivedBits.size < pattern.size) {
//            return false
//        }
//
//        for (i in 0..(receivedBits.size - pattern.size)) {
//            var match = true
//            for (j in pattern.indices) {
//                if (receivedBits[i + j] != pattern[j]) {
//                    match = false
//                    break
//                }
//            }
//            if (match) {
//                return true
//            }
//        }
//
//        return false
//    }

//    private fun byteArrayToShortArray(byteArray: ByteArray): ShortArray {
//        val n = byteArray.size / 2
//        val shortArray = ShortArray(n)
//        for (i in 0 until n) {
//            shortArray[i] = ((byteArray[2 * i + 1].toInt() shl 8) or (byteArray[2 * i].toInt() and 0xFF)).toShort()
//        }
//        return shortArray
//    }
}