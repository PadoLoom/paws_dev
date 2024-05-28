import 'package:flutter/material.dart';
import 'package:paws_dev/native_call/euphony_service.dart';

class ServiceSwitch extends StatefulWidget {
  const ServiceSwitch({super.key});

  @override
  State<ServiceSwitch> createState() => _ServiceSwitchState();
}

class _ServiceSwitchState extends State<ServiceSwitch> {
  bool isAct = true;
  @override
  Widget build(BuildContext context) {
    return Switch(
      value: isAct,
      activeColor: Colors.red,
      onChanged: (bool value) {
        if (!value) {
          EuphonyService().stopReceiver();
        } else {
          EuphonyService().startReceiver();
        }

        setState(() {
          isAct = value;
        });
      },
    );
  }
}
