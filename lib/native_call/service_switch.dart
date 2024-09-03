import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:developer' as developer;

class ServiceSwitch extends StatefulWidget {
  const ServiceSwitch({super.key});

  @override
  State<ServiceSwitch> createState() => _ServiceSwitchState();
}

class _ServiceSwitchState extends State<ServiceSwitch> {
  bool isAct = true;
  static const platform = MethodChannel('bpsk-native');

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
      await platform.invokeMethod(
        'startReceiver',
      );
      // developer.log("Received : $rLetters");
    } on PlatformException catch (e) {
      developer.log("Failed to start receiver: ${e.message}");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Switch(
      value: isAct,
      focusColor: Colors.redAccent,
      activeColor: Colors.blue,
      onChanged: (bool value) {
        if (!value) {
          stopReceiver();
        } else {
          startReceiver();
        }

        setState(() {
          isAct = value;
        });
      },
    );
  }
}
