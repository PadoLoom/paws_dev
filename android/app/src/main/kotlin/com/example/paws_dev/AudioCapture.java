package com.example.paws_dev;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.ByteArrayOutputStream;

public class AudioCapture {

    private AudioRecord audioRecord;
    private int bufferSize;

    @SuppressLint("MissingPermission")
    public void start() {
        bufferSize = AudioRecord.getMinBufferSize(
                48000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                48000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
        );

        audioRecord.startRecording();
    }

    public byte[] captureAudio(int durationMillis) {
        byte[] data = new byte[bufferSize];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int bytesToCapture = (int) (durationMillis / 1000.0 * 48000 * 2); // 16비트(2바이트) 샘플

        while (out.size() < bytesToCapture) {
            int numBytesRead = audioRecord.read(data, 0, data.length);
            if (numBytesRead > 0) {
                out.write(data, 0, numBytesRead);
            }
        }

        return out.toByteArray();
    }

    public void stop() {
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
    }
}
