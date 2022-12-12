
import 'udlib_platform_interface.dart';

class Udlib {
  Future<String?> getPlatformVersion() {
    return UdlibPlatform.instance.getPlatformVersion();
  }
}
