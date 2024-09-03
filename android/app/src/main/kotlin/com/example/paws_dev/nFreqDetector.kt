package hongik.capstone.bpsk

import org.jtransforms.fft.DoubleFFT_1D
import kotlin.math.sqrt


class nFreqDetector {
    fun detectFrequencyMagnitude(audioData: ByteArray): Double {
        val n = audioData.size / 2
        val samples = DoubleArray(n)
        for (i in 0 until n) {
            val sample = (audioData[2 * i + 1].toInt() shl 8) or (audioData[2 * i].toInt() and 0xFF)
            samples[i] = sample / 32768.0
        }

        val fft = DoubleFFT_1D(n.toLong())
        val fftData = DoubleArray(2 * n)
        System.arraycopy(samples, 0, fftData, 0, n)
        fft.realForwardFull(fftData)

        val targetIndex = Math.round(TARGET_FREQUENCY * n / SAMPLE_RATE).toInt()

        val re = fftData[2 * targetIndex]
        val im = fftData[2 * targetIndex + 1]
        val magnitude = sqrt(re * re + im * im)

        return magnitude
    }

    companion object {
        private const val TARGET_FREQUENCY = 19000.0
        private const val SAMPLE_RATE = 48000.0
    }
}