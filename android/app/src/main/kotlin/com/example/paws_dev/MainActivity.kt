package com.example.paws_dev

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import co.euphony.rx.AcousticSensor
import co.euphony.rx.EuRxManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel


class MainActivity: FlutterActivity() {

    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private var permissionToRecordAccepted = false
    private val permissions = arrayOf<String>(Manifest.permission.RECORD_AUDIO)

    private val CHANNEL = "euphony-native"
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



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        permissionToRecordAccepted =
            requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED
        if (!permissionToRecordAccepted) {
            finish()
        }
    }

    private fun requestAudioPermissions() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }


    private val mRxManager = EuRxManager()
    private fun startReceiver() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            // Start audio recording
            Toast.makeText(this, "Service Start", Toast.LENGTH_LONG).show()
            mRxManager.acousticSensor = AcousticSensor { letters ->
                Toast.makeText(this, letters, Toast.LENGTH_LONG).show()
            }
            mRxManager.listen()
        } else {
            requestAudioPermissions();
        }

    }
    private fun stopReceiver() {
        mRxManager.finish()
        Toast.makeText(this, "Service Terminated", Toast.LENGTH_LONG).show()
    }
}

