import 'dart:convert';

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
    List<Map<String,dynamic>> seasonEpisodes = [
      {
        "id": 64863,
        "title": "Episode 1",
        "order": "1",
        "poster_photo":
        "https://thekee-m.gcdn.co/images06012022/uploads/media/series/seasons/posters/2020-07-01/ZMx47Bf3sO03FprZ.jpg",
        "duration": "10 min",
        "download_url":
        "https://thekee.gcdn.co/video/m-159n/English/Animation&Family/Tom.and.Jerry.1965/01.mp4?md5=eCp0VmIS_doipZ6lGVxwVg&expires=1678550892",
        "hd_url":
        "https://thekee.gcdn.co/video/m-159n/English/Animation&Family/Tom.and.Jerry.1965/01.mp4?md5=eCp0VmIS_doipZ6lGVxwVg&expires=1678550892",
        "trailer_url": null,
        "media_url":
        "https://thekee.gcdn.co/video/m-159n/English/Animation&Family/Tom.and.Jerry.1965/01.mp4?md5=eCp0VmIS_doipZ6lGVxwVg&expires=1678550892",
        "created_at": "2020-07-01 13:27:14",
        "release_date": "2020-07-01 00:00:00",
        "watching": {
          "current_time": "13",
          "duration": "1380"
        }
      },
      {
        "id": 64864,
        "title": "Episode 2",
        "order": "2",
        "poster_photo":
        "https://thekee-m.gcdn.co/images06012022/uploads/media/series/seasons/posters/2020-07-01/ZMx47Bf3sO03FprZ.jpg",
        "duration": "10 min",
        "download_url":
        "https://thekee.gcdn.co/video/m-159n/English/Animation&Family/Tom.and.Jerry.1965/02.mp4?md5=eCp0VmIS_doipZ6lGVxwVg&expires=1678550892",
        "hd_url":
        "https://thekee.gcdn.co/video/m-159n/English/Animation&Family/Tom.and.Jerry.1965/02.mp4?md5=eCp0VmIS_doipZ6lGVxwVg&expires=1678550892",
        "trailer_url": null,
        "media_url":
        "https://thekee.gcdn.co/video/m-159n/English/Animation&Family/Tom.and.Jerry.1965/02.mp4?md5=eCp0VmIS_doipZ6lGVxwVg&expires=1678550892",
        "created_at": "2020-07-01 13:27:14",
        "release_date": "2020-07-01 00:00:00",
        "watching": {
          "current_time": "25",
          "duration": "60"
        }
      },
      {
        "id": 64865,
        "title": "Episode 3",
        "order": "3",
        "poster_photo":
        "https://thekee-m.gcdn.co/images06012022/uploads/media/series/seasons/posters/2020-07-01/ZMx47Bf3sO03FprZ.jpg",
        "duration": "10 min",
        "download_url":
        "https://thekee.gcdn.co/video/m-159n/English/Animation&Family/Tom.and.Jerry.1965/03.mp4?md5=eCp0VmIS_doipZ6lGVxwVg&expires=1678550892",
        "hd_url":
        "https://thekee.gcdn.co/video/m-159n/English/Animation&Family/Tom.and.Jerry.1965/03.mp4?md5=eCp0VmIS_doipZ6lGVxwVg&expires=1678550892",
        "trailer_url": null,
        "media_url":
        "https://thekee.gcdn.co/video/m-159n/English/Animation&Family/Tom.and.Jerry.1965/03.mp4?md5=eCp0VmIS_doipZ6lGVxwVg&expires=1678550892",
        "created_at": "2020-07-01 13:27:14",
        "release_date": "2020-07-01 00:00:00",
        "watching": null
      },
      {
        "id": 64866,
        "title": "Episode 4",
        "order": "4",
        "poster_photo":
        "https://thekee-m.gcdn.co/images06012022/uploads/media/series/seasons/posters/2020-07-01/ZMx47Bf3sO03FprZ.jpg",
        "duration": "10 min",
        "download_url":
        "https://thekee.gcdn.co/video/m-159n/English/Animation&Family/Tom.and.Jerry.1965/04.mp4?md5=eCp0VmIS_doipZ6lGVxwVg&expires=1678550892",
        "hd_url":
        "https://thekee.gcdn.co/video/m-159n/English/Animation&Family/Tom.and.Jerry.1965/04.mp4?md5=eCp0VmIS_doipZ6lGVxwVg&expires=1678550892",
        "trailer_url": null,
        "media_url":
        "https://thekee.gcdn.co/video/m-159n/English/Animation&Family/Tom.and.Jerry.1965/04.mp4?md5=eCp0VmIS_doipZ6lGVxwVg&expires=1678550892",
        "created_at": "2020-07-01 13:27:14",
        "release_date": "2020-07-01 00:00:00",
        "watching": null
      }
    ];
    dynamic result;
    try {
      result =
          await _udlibPlugin.play(
            {
              "title": "title",
              "id": '205727',
              "type": "series",
              "description": "here is the description",
              "posterPhoto": 'https://thekee-m.gcdn.co/images06012022/uploads/media/series/posters/2022-09-27/0ObHcBVUnfpzbtIB.jpg',
              "mediaUrl": "https://thekee.gcdn.co/video/m-159n/English/Animation&Family/Baby.Shark.Best.Kids.Song/S01/01.mp4",
              "playPosition": '9000',
              "userId": '77810',
              "profileId": '217588',
              "mediaType": "tvshow",
              "episodes": jsonEncode(seasonEpisodes),
              "subtitles": jsonEncode([]),
              "subtitle": "-",
              "episode_position":1
            }
          ) ?? 'UnKnown Result from play';
    } on PlatformException {
      result = 'Failed to play.';
    }
    if(kDebugMode) {
      print("result: $result");
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
