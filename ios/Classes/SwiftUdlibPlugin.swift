import Flutter
import UIKit

import Foundation
import AVKit

public class SwiftUdlibPlugin: NSObject, FlutterPlugin {
    var avPlayer: AVPlayer!
    var avPlayerViewController: AVPlayerViewController!
    var avPlayerItem: AVPlayerItem!
    var avAsset: AVAsset!
    var playerItemContext = 0
    var avPlayerItemStatus: AVPlayerItem.Status = .unknown
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "udlib", binaryMessenger: registrar.messenger())
        
        let instance = SwiftUdlibPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        let flutterViewController: UIViewController =
        (UIApplication.shared.delegate?.window??.rootViewController)!;
        switch(call.method){
        case "play":
            self.play(result: result, call: call, controller: flutterViewController)
            break;
        default:
            print("method wasn't found : ",call.method);
        }
    }
    
    func play(result: @escaping FlutterResult,call: FlutterMethodCall,controller : UIViewController){
        guard let args = call.arguments else {
            return
        }
        if let myArgs = args as? [String: Any],
           let videoUrl : String = myArgs["video_url"] as? String
        {
            let videoURL = URL(string: videoUrl)
            self.avAsset = AVAsset(url: videoURL!)
            self.avPlayerItem = AVPlayerItem(asset: self.avAsset)
            self.avPlayer = AVPlayer(playerItem: self.avPlayerItem)
            self.avPlayerViewController = AVPlayerViewController()
            self.avPlayerViewController.player = self.avPlayer
            self.avPlayerViewController.allowsPictureInPicturePlayback = true
            
            controller.present(self.avPlayerViewController, animated: true) {
                try! AVAudioSession.sharedInstance().setCategory(AVAudioSession.Category.playback, options: [])
                if #available(iOS 13.0, *) {
                    self.avPlayerViewController.isModalInPresentation = true
                } else {
                    self.avPlayerViewController.modalPresentationStyle = .fullScreen
                }
                
                if(self.avPlayer != nil){
                    if #available(iOS 10.0, *) {
                        self.avPlayerItem?.preferredForwardBufferDuration = TimeInterval(5)
                        self.avPlayer?.automaticallyWaitsToMinimizeStalling = self.avPlayerItem?.isPlaybackBufferEmpty ?? false
                        self.avPlayer?.currentItem?.preferredForwardBufferDuration = TimeInterval(5)
                        self.avPlayer?.play()
                    } else {
                        self.avPlayer?.play()
                    }
                }
            }
            result(true)
        } else {
            print("iOS could not extract flutter arguments in method: (startOfflineVideo)")
            result(false)
        }
        result(false)
        
    }
}
