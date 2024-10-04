import 'package:flutter/material.dart';

class PlaybackControlSwitch extends StatefulWidget {
  const PlaybackControlSwitch({super.key});

  @override
  State<PlaybackControlSwitch> createState() => _PlaybackControlSwitchState();
}

class _PlaybackControlSwitchState extends State<PlaybackControlSwitch> {
  bool isAct = true;

  void playbackControl(enable) {
    if (enable) {
      // TODO: implement
    } else {
      // TODO: implement
    }
  }

  @override
  Widget build(BuildContext context) {
    return Switch(
      value: isAct,
      activeColor: Colors.blueGrey,
      onChanged: (bool value) {
        if (!value) {
          playbackControl(true);
        } else {
          playbackControl(false);
        }

        setState(() {
          isAct = value;
        });
      },
    );
  }
}
