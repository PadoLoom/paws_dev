import 'package:flutter/material.dart';
import 'package:paws_dev/native_call/euphony_service.dart';

class ServiceSwitch extends StatefulWidget {
  const ServiceSwitch({super.key});

  @override
  State<ServiceSwitch> createState() => _ServiceSwitchState();
}

class _ServiceSwitchState extends State<ServiceSwitch> {
  bool isAct = false;
  @override
  Widget build(BuildContext context) {
    return Switch(
      value: isAct,
      onChanged: (bool isAct) {
        if (isAct) {
          EuphonyService().stopReceiver();
        } else {
          EuphonyService().startReceiver();
        }
      },
    );
  }
}
