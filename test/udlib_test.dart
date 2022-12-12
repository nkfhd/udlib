import 'package:flutter_test/flutter_test.dart';
import 'package:udlib/udlib.dart';
import 'package:udlib/udlib_platform_interface.dart';
import 'package:udlib/udlib_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockUdlibPlatform
    with MockPlatformInterfaceMixin
    implements UdlibPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final UdlibPlatform initialPlatform = UdlibPlatform.instance;

  test('$MethodChannelUdlib is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelUdlib>());
  });

  test('getPlatformVersion', () async {
    Udlib udlibPlugin = Udlib();
    MockUdlibPlatform fakePlatform = MockUdlibPlatform();
    UdlibPlatform.instance = fakePlatform;

    expect(await udlibPlugin.getPlatformVersion(), '42');
  });
}
