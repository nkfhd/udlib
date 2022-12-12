import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:udlib/udlib.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _udlibPlugin = Udlib();

  @override
  void initState() {
    super.initState();
  }

  _play() async {
    String result;
    try {
      result =
          await _udlibPlugin.play("https://thekee.gcdn.co/video/m-159n/English/Animation&Family/Baby.Shark.Best.Kids.Song/S01/01.mp4") ?? false ? "Success" : "Failed";
    } on PlatformException {
      result = 'Failed to play.';
    }
    if(kDebugMode) {
      print(result);
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: ElevatedButton(
            onPressed: _play,
            child: const Text('Play'),
          ),
        ),
      ),
    );
  }
}
