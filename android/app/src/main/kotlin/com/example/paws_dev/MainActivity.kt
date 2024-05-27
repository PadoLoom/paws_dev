package com.example.paws_dev

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
                    val code = call.argument<String>("code")
                    startTransmitter(code)
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

    private fun startReceiver() {
        val mRxManager = EuRxManager
        val mRxManager = EuRxManager.getInstance()
        mRxManager.acousticSensor = AcousticSensor { letters ->
            // Communicate back to Flutter using MethodChannel if needed
        }
        mRxManager.listen()
    }
}
}
