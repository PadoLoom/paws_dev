package hongik.capstone.bpsk;


import android.annotation.SuppressLint;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class BPSK {
    private static final int BUFFER_SIZE = 1024;  // 버퍼의 크기
    private static final String TARGET_PATTERN = "01000001";  // 종료 조건 패턴
    private static final int SAMPLE_RATE = 48000;  // 48.0 kHz
    private static final int CARRIER_FREQ = 19000; // 19 kHz
    private static final double BIT_DURATION = 1.0 / 128; // 0.0625초 (1초에 16비트)

    private static final int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
            android.media.AudioFormat.CHANNEL_IN_MONO,
            android.media.AudioFormat.ENCODING_PCM_16BIT) * 16 ;
    private static final String TAG = "BPSK_MAIN";
    private AudioRecord audioRecord;
    private boolean isRecording = false;

    @SuppressLint("MissingPermission")
    public void startDecoding() {
//        int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
//                android.media.AudioFormat.CHANNEL_IN_MONO,
//                android.media.AudioFormat.ENCODING_PCM_16BIT);

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                android.media.AudioFormat.CHANNEL_IN_MONO,
                android.media.AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize);

        isRecording = true;
        audioRecord.startRecording();

//        detectPattern();
    }
    public boolean detectPattern() {
        AtomicBoolean detect = new AtomicBoolean(false);
        Log.i(TAG, "detectPattern()");
        Log.i(TAG, "bufferSize: " + minBufferSize);

        Thread detectionThread = new Thread(() -> {
            byte[] audioData = new byte[minBufferSize];
            FreqDetector detector = new FreqDetector();

            while (isRecording) {
                int bytesRead = audioRecord.read(audioData, 0, audioData.length);
                if (bytesRead > 0) {
                    double magnitude = detector.detectFrequencyMagnitude(audioData);
                    if (magnitude >= 0.3) { // 주파수 크기가 임계값 이상인 경우
                        try {
                            int[] decodedBits = Decoder.decodeAudioData(audioData, CARRIER_FREQ, SAMPLE_RATE, BIT_DURATION);
                            String bitString = convertBitsToString(decodedBits);

                            // 종료 조건 패턴이 bitString에 포함되어 있는지 확인
                            if (bitString.contains(TARGET_PATTERN)) {
                                stopDecoding();
                                Log.i(TAG, "bitString detected");
                                detect.set(true);
                                break; // 패턴이 감지되면 루프를 종료합니다.
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    Thread.sleep(100); // 0.5초 대기
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        detectionThread.start();

        try {
            detectionThread.join(); // 메인 쓰레드가 detectPattern 메소드에서 반환되기 전에 detectionThread의 종료를 기다립니다.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return detect.get();
    }

    public void stopDecoding() {
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }

    private String convertBitsToString(int[] bits) {
        StringBuilder sb = new StringBuilder();
        for (int bit : bits) {
            sb.append(bit);
        }
        return sb.toString();
    }
}
