import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'udlib_method_channel.dart';

abstract class UdlibPlatform extends PlatformInterface {
  /// Constructs a UdlibPlatform.
  UdlibPlatform() : super(token: _token);

  static final Object _token = Object();

  static UdlibPlatform _instance = MethodChannelUdlib();

  /// The default instance of [UdlibPlatform] to use.
  ///
  /// Defaults to [MethodChannelUdlib].
  static UdlibPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [UdlibPlatform] when
  /// they register themselves.
  static set instance(UdlibPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<dynamic> play(Map<String,dynamic> data) {
    throw UnimplementedError('play() has not been implemented.');
  }

  Future<dynamic> playOffline(Map<String,dynamic> data) {
    throw UnimplementedError('playOffline() has not been implemented.');
  }
}
