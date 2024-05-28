package com.example.paws_dev

import android.widget.Toast
import co.euphony.rx.AcousticSensor
import co.euphony.rx.EuRxManager
import co.euphony.tx.EuTxManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.KeyData.CHANNEL
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {

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
    private val mRxManager = EuRxManager()
    private fun startReceiver() {
        Toast.makeText(this, "Service Start", Toast.LENGTH_LONG).show()
        mRxManager.acousticSensor = AcousticSensor { letters ->
            Toast.makeText(this, letters, Toast.LENGTH_LONG).show()
        }
        mRxManager.listen()
    }
    private fun stopReceiver() {
        mRxManager.finish()
        Toast.makeText(this, "Service Terminated", Toast.LENGTH_LONG).show()
    }
}

