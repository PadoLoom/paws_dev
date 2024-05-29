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
  static const platform = MethodChannel('euphony-native');

  // var servicestate = "Activated";

  Future<void> stopReceiver() async {
    try {
      await platform.invokeMethod(
        'stopReceiver',
      );
      // servicestate = "Terminated";
    } on PlatformException catch (e) {
      developer.log("Failed to stop transmitter: ${e.message}");
    }
  }

  Future<void> startReceiver() async {
    try {
      String rLetters = await platform.invokeMethod('startReceiver');
      developer.log("Received : $rLetters");
      setState(() {});
    } on PlatformException catch (e) {
      developer.log("Failed to start receiver: ${e.message}");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Switch(
      value: isAct,
      activeColor: Colors.red,
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
