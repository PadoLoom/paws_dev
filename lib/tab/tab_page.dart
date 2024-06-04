import 'package:paws_dev/tab/setting/setting_page.dart';
import 'package:paws_dev/tab/home/home_page.dart';
import 'package:flutter/material.dart';
import 'package:paws_dev/theme/colors.dart';

class TabPage extends StatefulWidget {
  const TabPage({super.key});

  @override
  State<TabPage> createState() => _TabPageState();
}

class _TabPageState extends State<TabPage> {
  int _currentIndex = 0;

  final _pages = [
    const HomePage(),
    const SettingPage(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _pages[_currentIndex],
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _currentIndex,
        onTap: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
        items: const [
          BottomNavigationBarItem(
            icon: Icon(
              Icons.settings,
              color: mainFontColor,
            ),
            label: 'Settings',
          ),
          BottomNavigationBarItem(
            icon: Icon(
              Icons.info,
              color: mainFontColor,
            ),
            label: 'Description',
          ),
        ],
      ),
    );
  }
}
