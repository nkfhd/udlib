package com.udlib.udlib

import android.app.Activity
import android.app.Dialog
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import com.actsol.thekee.Episode
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.Util
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.flutter.Log
import java.io.File
import java.lang.StringBuilder

class OfflineVideo : Activity() {
    companion object {
        val TAG: String = OfflineVideo::class.java.getSimpleName()

        var player: SimpleExoPlayer? = null
        var playerView: PlayerView? = null

        var mediaUrl: String? = ""
        var playPosition: Long? = 0
        var userId: String? = null
        var mediaId: String? = null
        var profileId: String? = null
        var fileName: String? = null
        var localFile: String? = null

        //        var videoSource: MediaSource? = null
        var mediaItem: MediaItem? = null
        var mediaType: String? = null
        var episodePosition: Int? = null
        var episodesList: List<Episode>? = null

        var trackSelector: DefaultTrackSelector? = null
        var audioBtn: ImageButton? = null
        var back_btn: ImageButton? = null
        var audioLinear: LinearLayout? = null
        var nextEpisodeLinear: LinearLayout? = null
        var prevEpisodeLinear: LinearLayout? = null
        var ccLienar: LinearLayout? = null
        var audiosList = arrayOf("Auto", "Arabic")
        var ccList = arrayOf("Auto", "Arabic");
        private var PRIVATE_MODE = 0
        private val PREF_NAME = "thekee_shared_prefs"
        var sharedPref: SharedPreferences? = null;

        var isDecryptedBefore: Boolean = false

        lateinit var fileMedia: File
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decoder(base64Str: String, pathFile: String): Unit {
        val imageByteArray = java.util.Base64.getDecoder().decode(base64Str)
        File(pathFile).writeBytes(imageByteArray)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_offline_video)

        mediaUrl = intent.getStringExtra("mediaUrl")
        playPosition = 0
        userId = intent.getStringExtra("userId")
        mediaId = intent.getStringExtra("mediaId")
        profileId = intent.getStringExtra("profileId")
        mediaId = intent.getStringExtra("mediaId")
        mediaType = intent.getStringExtra("mediaType")
        fileName = intent.getStringExtra("fileName")
        Log.d(TAG, "mediaUrl : " + mediaUrl)
        Log.d(TAG, "playPosition : " + playPosition)
        Log.d(TAG, "userId : " + userId)
        Log.d(TAG, "mediaId : " + mediaId)
        fileMedia = File(filesDir.absolutePath + File.separator + mediaId.toString() + ".mp4")
        Log.d(TAG, "fileMedia : " + fileMedia.absolutePath)
        if (!fileMedia.exists()) {
            var sb = StringBuilder()
            sb.append(getExternalFilesDir(null))
            sb.append(java.io.File.separator)
            sb.append(mediaUrl.toString().replace("/", java.io.File.separator))
            sb.append(java.io.File.separator)
            sb.append(fileName)
            fileMedia = File(sb.toString())
            Log.d(TAG, "fileMedia : " + fileMedia.absolutePath)
        }
        Log.d(TAG, fileMedia.absolutePath);
        playerView = findViewById(R.id.player_view)
        audioBtn = findViewById(R.id.btn_audio);
        back_btn = findViewById(R.id.back_btn);
        nextEpisodeLinear = findViewById(R.id.next_episode_ll);
        prevEpisodeLinear = findViewById(R.id.prev_episode_ll);
        audioLinear = findViewById(R.id.audio_ll);
        ccLienar = findViewById(R.id.cc_ll);
        audioBtn?.visibility = View.INVISIBLE
        audioLinear?.visibility = View.INVISIBLE
        nextEpisodeLinear?.visibility = View.INVISIBLE
        prevEpisodeLinear?.visibility = View.INVISIBLE
        ccLienar?.visibility = View.INVISIBLE
        back_btn?.setOnClickListener {
            finish();
        }
        if (intent.getStringExtra("mediaType").equals("tvshow")) {
            episodePosition = Integer.parseInt(intent.getStringExtra("episode_position"))
            var episodesString = intent.getStringExtra("episodes")
            val gson = GsonBuilder().create()
            episodesList = gson.fromJson<ArrayList<Episode>>(
                episodesString,
                object : TypeToken<ArrayList<Episode>>() {}.type
            )
            Log.d(TAG, "episodesList : " + episodesList?.size)
            //initializePlayerItems();
            nextEpisodeLinear?.visibility = View.INVISIBLE
            prevEpisodeLinear?.visibility = View.INVISIBLE
        }

        playerInitiate()
    }

    private fun playerInitiate() {
        trackSelector = DefaultTrackSelector(applicationContext)
        trackSelector?.buildUponParameters()
            ?.setMaxVideoSizeSd()
            ?.setPreferredAudioLanguage("en")

        player = SimpleExoPlayer.Builder(applicationContext)
            .setTrackSelector(trackSelector!!)
            .build()

        playerView?.player = player

        player?.playWhenReady = true
        player?.addListener(PlayerEventListener())

        var dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            applicationContext, Util.getUserAgent(
                baseContext, getString(
                    R.string.app_name
                )
            )
        )
//        var mergedMediaSource = MergingMediaSource()
//        videoSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(fileMedia.toString()))
//        mergedMediaSource = MergingMediaSource(videoSource)
//        player?.prepare(mergedMediaSource)
//        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(fileMedia.toString()))
        player!!.setMediaSource(mediaSource)
        player!!.prepare()
        player?.seekTo(playPosition!!)
        playerView?.setKeepScreenOn(true)

        audioLinear?.setOnClickListener {
            showAudioDialog(this)
            var mappedTrackInfo: MappingTrackSelector.MappedTrackInfo =
                Assertions.checkNotNull(trackSelector?.getCurrentMappedTrackInfo());
            var parameters: DefaultTrackSelector.Parameters = trackSelector?.getParameters()!!
            for (rendererIndex in 0 until mappedTrackInfo.getRendererCount()) {
                var trackType: Int = mappedTrackInfo.getRendererType(rendererIndex)
                var trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)
                var isRendererDisabled = parameters.getRendererDisabled(rendererIndex);
                var selectionOverride =
                    parameters.getSelectionOverride(rendererIndex, trackGroupArray)
                for (groupIndex in 0 until trackGroupArray.length) {
                    for (trackIndex in 0 until trackGroupArray.get(groupIndex).length) {
                        var trackName = DefaultTrackNameProvider(getResources()).getTrackName(
                            trackGroupArray.get(groupIndex).getFormat(trackIndex)
                        )
                        var isTrackSupported = mappedTrackInfo.getTrackSupport(
                            rendererIndex,
                            groupIndex,
                            trackIndex
                        ) == RendererCapabilities.FORMAT_HANDLED
                        Log.d(
                            TAG,
                            "track item " + groupIndex + ": trackName: " + trackName + ", isTrackSupported: " + isTrackSupported
                        )
                    }
                }
            }
        }

        ccLienar?.setOnClickListener {
            showSubtitleDialog(this)
        }
    }


    class PlayerEventListener : Player.Listener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            playerView?.setKeepScreenOn(true)
            if (playWhenReady && playbackState == Player.STATE_READY) { // media actually playing
                Log.d(TAG, "Video Played")
                Log.d(TAG, "Video now playing at : " + player?.getCurrentPosition()!! / 1000)
            } else if (playWhenReady) {
                Log.d(TAG, "Video buffering,preparing,finished")

            } else { // player paused in any state
                Log.d(TAG, "Video Paused")
                Log.d(TAG, "Video now played at : " + player?.getCurrentPosition()!! / 1000)
            }
        }

        override fun onPlayerError(error: PlaybackException) {

        }


        override fun onSeekProcessed() {
            Log.d(TAG, "Seeeeeeeek")
        }

        override fun onTracksChanged(
            tracks: Tracks
        ) {
            super.onTracksChanged(tracks)
            val trackGroups = tracks.groups
            var audios = 0;
            var subtitlesTracks = 0;
            Log.d(TAG, "Track Changed " + trackGroups.size)
            val mappedTrackInfo: MappingTrackSelector.MappedTrackInfo? =
                trackSelector?.currentMappedTrackInfo
            if (mappedTrackInfo != null) {
                if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                    == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS
                ) {
                    Log.d(TAG, "Not Supported Video Track")
                }
            }
            if (mappedTrackInfo != null) {
                if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO)
                    == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS
                ) {
                    Log.d(TAG, "Not Supported Audio Track")
                }
            }

            if (!trackGroups.isEmpty()) {
                for (arrayIndex in 0 until trackGroups.size) {
                    for (groupIndex in 0 until trackGroups[arrayIndex].length) {
                        val sampleMimeType =
                            trackGroups[arrayIndex].getTrackFormat(groupIndex).sampleMimeType
                        if (sampleMimeType != null && sampleMimeType.contains("audio")) {
                            //video contains audio
                            Log.d(TAG, sampleMimeType)
                            Log.d(TAG, "HAS AUDIO")
                            audios = audios + 1
                            if (audios > 1) {
                                audioBtn?.visibility = View.VISIBLE
                                audioLinear?.visibility = View.VISIBLE
                            }
                        } else if (sampleMimeType != null && sampleMimeType.contains("x-quicktime-tx3g")) {
                            Log.d(TAG, "HAS Subtitle " + sampleMimeType.toString())
                            subtitlesTracks = subtitlesTracks + 1
                            if (audios > 1) {
                                ccLienar?.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }

    }


    override fun onDestroy() {
        player?.stop()
        super.onDestroy()
    }


    fun showSubtitleDialog(activity: Activity) {

        var dialog: Dialog = Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_listview)

        var btndialog: Button = dialog.findViewById(R.id.btndialog)
        btndialog.setOnClickListener { dialog.dismiss() }


        var listView: ListView = dialog.findViewById(R.id.listview)
        var arrayAdapter = ArrayAdapter(
            this,
            R.layout.list_item,
            R.id.tv,
            ccList
        );
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener { parent, view, position, id ->
            //Toast.makeText(activity, "You have clicked : " + audiosList[position], Toast.LENGTH_SHORT).show();
            var builder: DefaultTrackSelector.Parameters.Builder =
                trackSelector!!.buildUponParameters()
            if (position == 0) {
                //mergedSource = new MergingMediaSource(videoSource, subtitleSourceEN,subtitleSourceAR);
                builder.setMaxVideoSizeSd()
                    .setPreferredTextLanguage("en")
                Log.d(TAG, "Changes to English")
            } else {
                // mergedSource = new MergingMediaSource(videoSource, subtitleSourceAR,subtitleSourceEN);
                builder.setMaxVideoSizeSd()
                    .setPreferredTextLanguage("ar")
                Log.d(TAG, "Changes to Arabic")
            }

            trackSelector?.setParameters(builder)
            dialog.dismiss();


        }


        dialog.show();

    }

    fun showAudioDialog(activity: Activity) {

        var dialog: Dialog = Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_listview)

        var btndialog: Button = dialog.findViewById(R.id.btndialog)
        btndialog.setOnClickListener { dialog.dismiss() }


        var listView: ListView = dialog.findViewById(R.id.listview)
        var arrayAdapter = ArrayAdapter(
            this,
            R.layout.list_item,
            R.id.tv,
            audiosList
        );
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener { parent, view, position, id ->
            //Toast.makeText(activity, "You have clicked : " + audiosList[position], Toast.LENGTH_SHORT).show();
            var builder: DefaultTrackSelector.Parameters.Builder =
                trackSelector!!.buildUponParameters()
            if (position == 0) {
                //mergedSource = new MergingMediaSource(videoSource, subtitleSourceEN,subtitleSourceAR);
                builder.setMaxVideoSizeSd()
//                        .setPreferredTextLanguage("en")
                    .setPreferredAudioLanguage("en")
                Log.d(TAG, "Changes to english")
            } else {
                // mergedSource = new MergingMediaSource(videoSource, subtitleSourceAR,subtitleSourceEN);
                builder.setMaxVideoSizeSd()
//                        .setPreferredTextLanguage("ar")
                    .setPreferredAudioLanguage("ar")
                Log.d(TAG, "Changes to Arabic")
            }

            trackSelector?.setParameters(builder)
            dialog.dismiss();


        }


        dialog.show();

    }

    override fun onPause() {
        player?.stop()
        super.onPause()
    }

}