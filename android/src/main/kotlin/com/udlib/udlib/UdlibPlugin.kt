package com.udlib.udlib

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

class UdlibPlugin : FlutterPlugin, MethodCallHandler, ActivityAware,
    PluginRegistry.ActivityResultListener {
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var activity: Activity

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "udlib")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        Log.d("UdlibPlugin", "onMethodCall: ${call.method}")
        if (call.method == "play") {
            play(call, result)
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else {
            result.notImplemented()
        }
    }

    private fun play(call: MethodCall, result: MethodChannel.Result) {
        val mediaUrl: String? = call.argument("mediaUrl")
        val playPosition: String? = call.argument("playPosition")
        val userId: String? = call.argument("userId")
        val profileId: String? = call.argument("profileId")
        val id: String? = call.argument("id")
        val mediaType: String? = call.argument("mediaType")
        val subtitles: String? = call.argument("subtitles")

        val intent = Intent(activity, PlayerActivity::class.java)
        intent.putExtra("mediaUrl", mediaUrl)
        intent.putExtra("playPosition", playPosition)
        intent.putExtra("userId", userId)
        intent.putExtra("mediaId", id)
        intent.putExtra("profileId", profileId)
        intent.putExtra("mediaType", mediaType)
        intent.putExtra("subtitles", subtitles)

        if (mediaType.toString().toLowerCase().equals("tvshow")) {
            Log.d("TVSHOW_HERE", "TVSHOW_HERE");
            var episode_position: String? = call.argument<String?>("episode_position").toString()
            var episodes: String? = call.argument("episodes")
            intent.putExtra("episode_position", episode_position)
            intent.putExtra("episodes", episodes)
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
        ActivityCompat.startActivityForResult(activity, intent, 833831, null)
    }


    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity;

    }

    override fun onDetachedFromActivityForConfigChanges() {
        TODO("Not yet implemented")
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        TODO("Not yet implemented")
    }

    override fun onDetachedFromActivity() {
        TODO("Not yet implemented")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == 833831) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("UdlibPlugin", "onActivityResult")
                // Get String data from Intent
//                val returnString = data!!.getStringExtra("result_data")
//                globalResult?.success(returnString);
            }
        }
        return true
    }
}
