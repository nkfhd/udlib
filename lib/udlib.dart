
import 'udlib_platform_interface.dart';

class Udlib {
  Future<dynamic> play(Map<String,dynamic> data) {
    return UdlibPlatform.instance.play(data);
  }
}
