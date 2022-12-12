
import 'udlib_platform_interface.dart';

class Udlib {
  Future<bool?> play(String url) {
    return UdlibPlatform.instance.play(url);
  }
}
