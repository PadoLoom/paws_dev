import 'package:flutter/material.dart';

const List<String> typeList = <String>['1','2','3','4','5'];

class SelectButtonExample extends StatefulWidget {
  const SelectButtonExample({super.key});

  @override
  State<SelectButtonExample> createState() => _SelectButtonExampleState();
}

class _SelectButtonExampleState extends State<SelectButtonExample> {
  String downValue = typeList.first;

  @override
  Widget build(BuildContext context) {
    return DropdownButton<String>(
      value: downValue,
      icon: const Icon(Icons.arrow_drop_down),
      autofocus: true,
      elevation: 16,
      style: const TextStyle(color: Colors.black),
      underline: Container(
        height: 2,
        color: Colors.grey,
      ),
      onChanged: (String? value) {
        // This is called when the user selects an item.
        setState(() {
          downValue = value!;
        });
      },
      items: typeList.map<DropdownMenuItem<String>>((String value) {
        return DropdownMenuItem<String>(
          value: value,
          child: Text(value),
        );
      }).toList(),
    );
  }
}