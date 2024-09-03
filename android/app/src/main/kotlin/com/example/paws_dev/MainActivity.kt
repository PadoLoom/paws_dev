package com.example.paws_dev

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import co.euphony.rx.EuRxManager
import hongik.capstone.bpsk.nBPSK
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity: FlutterActivity() {

    private val STRENGTH = "NORM"
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private val PERMISSIONS_REQUEST_CODE = 100
    private var permissionToRecordAccepted = false
    private val permissions = arrayOf<String>(Manifest.permission.RECORD_AUDIO)

    private val CHANNEL = "bpsk-native"
    private val mRxManager = EuRxManager.getInstance()
    private val TAG = "NATIVE ACTIVITY"


    private var isReceiving = false
    private var bpskJob: Job? = null
    private val nBPSK = nBPSK()
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
                call, result ->
            when (call.method) {
                "stopReceiver" -> {
                    stopReceiver()
                    result.success(null)
                }

                "startReceiver" -> {
                    startReceiver()
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }
    }


//    private fun requestAudioPermissions() {
//        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
//    }

    private fun checkPermissions(): Boolean {
        val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        val audioPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

        return notificationPermission && audioPermission
    }
    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionsToRequest.add(android.Manifest.permission.RECORD_AUDIO)

        ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), PERMISSIONS_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            val permissionResults = permissions.zip(grantResults.toTypedArray()).toMap()
            val allPermissionsGranted = permissionResults.all { it.value == PackageManager.PERMISSION_GRANTED }

            if (allPermissionsGranted) {
                // 권한이 모두 허용되었을 때
                showNotification("Title", "Content", STRENGTH)
            } else {
                // 권한이 거부되었을 때
                // 필요한 동작을 정의합니다.
            }
        }
    }

    private fun showNotification(title: String, content: String, option: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1
        val channelId = "paws_notification_channel"
        val channelName = "PAWS Alert"
        val description = "Channel of PAWS Alert Notification"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.description = description
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100L, 200L, 300L)

            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                0
            })


        if(option == "LOW"){
            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            notificationManager.notify(notificationId, builder.build())

        } else if (option == "NORM" || option == "HIGH") {
            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)

            notificationManager.notify(notificationId, builder.build())

        }

    }

    // -- Euphony Method for Rx Start & Stop -- //

//    private fun startReceiver() {
//        mRxManager.setOption(EuOption.builder()
//            .modeWith(EuOption.ModeType.EUPI)
//            .encodingWith(EuOption.CodingType.BASE16)
//            .modulationWith((EuOption.ModulationType.FSK))
//            .build())
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
//            // Start audio recording
//            Toast.makeText(this, "Service Start", Toast.LENGTH_SHORT).show()
//            Log.v(TAG, "startReceiver")
//
//            mRxManager.setOnWaveKeyUp(19000){
//                Log.d(TAG, "----------------[Key Up Received        ]----------------")
//                showNotification("Warning", "COLLISION HAZARD!", "NORM")
//                mRxManager.finish()
//                Log.d(TAG, "----------------[setOnWaveKeyUp Restart ]----------------")
//                mRxManager.listen()
//            }
//            mRxManager.listen()
//
//
//        } else {
//            requestPermissions()
//        }
//
//    }
//    private fun stopReceiver() {
//        mRxManager.finish()
//        Toast.makeText(this, "Service Terminated", Toast.LENGTH_SHORT).show()
//        Log.v(TAG,"stopReceiver")
//    }


    // -- Custom BPSK Method for Rx Start & Stop -- //


    private fun startReceiver() {

        isReceiving = true
        val pattern = shortArrayOf(0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1) // 16bit


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            bpskJob = CoroutineScope(Dispatchers.IO).launch {
                val isPatternDetected = withContext(Dispatchers.IO){
                    nBPSK.startReceiving(pattern)
                    }
                // 디코딩 중 문자열을 감지하면 true 반환
                if(isPatternDetected) {
                    showNotification("Warning", "COLLISION HAZARD!", "NORM")
                }


            }

        } else {
            requestPermissions()
        }

    }

    private fun stopReceiver() {
        isReceiving = false
        bpskJob?.cancel() // 코루틴 취소
        nBPSK.stopReceiving() // 디코딩 중단
        Toast.makeText(this, "Service Terminated", Toast.LENGTH_SHORT).show()
    }

}

