import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:udlib/udlib_method_channel.dart';

void main() {
  MethodChannelUdlib platform = MethodChannelUdlib();
  const MethodChannel channel = MethodChannel('udlib');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
