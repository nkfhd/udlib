import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'udlib_platform_interface.dart';

/// An implementation of [UdlibPlatform] that uses method channels.
class MethodChannelUdlib extends UdlibPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('udlib');

  @override
  Future<bool?> play(String url) async {
    final version = await methodChannel.invokeMethod<bool>('play',{"video_url":url});
    return version;
  }
}
