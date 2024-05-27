import 'package:flutter/services.dart';
import 'dart:developer' as developer;

class EuphonyService {
  static const platform = MethodChannel('euphony-native');

  Future<void> stopReceiver() async {
    try {
      await platform.invokeMethod(
        'stopReceiver',
      );
    } on PlatformException catch (e) {
      developer.log("Failed to stop transmitter: ${e.message}");
    }
  }

  Future<void> startReceiver() async {
    try {
      await platform.invokeMethod('startReceiver');
    } on PlatformException catch (e) {
      developer.log("Failed to start receiver: ${e.message}");
    }
  }
}
