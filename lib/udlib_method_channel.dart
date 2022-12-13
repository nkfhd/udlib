import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'udlib_platform_interface.dart';

/// An implementation of [UdlibPlatform] that uses method channels.
class MethodChannelUdlib extends UdlibPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('udlib');

  @override
  Future<dynamic> play(Map<String,dynamic> data) async {
    return await methodChannel.invokeMethod('play',data);
  }
}
