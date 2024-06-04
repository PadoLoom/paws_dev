import 'package:flutter/material.dart';
import 'package:paws_dev/theme/colors.dart';

class MainBox extends StatelessWidget {
  const MainBox({super.key});

  @override
  Widget build(BuildContext context) {
    var size = MediaQuery.of(context).size;
    return Column(
      children: [
        const SizedBox(
          width: 70,
          height: 70,
          child: Icon(
            Icons.warning_rounded,
            color: mainFontColor,
            size: 60.0,
          ),
        ),
        const SizedBox(
          height: 10,
        ),
        SizedBox(
          width: (size.width - 40) * 0.6,
          child: const Column(
            children: [
              Text(
                "PAWS",
                style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                    color: mainFontColor),
              ),
              SizedBox(
                height: 10,
              ),
              Text(
                "Pedestrian Accident Warning System",
                style: TextStyle(
                    fontSize: 12, fontWeight: FontWeight.w300, color: black),
              ),
            ],
          ),
        )
      ],
    );
  }
}
