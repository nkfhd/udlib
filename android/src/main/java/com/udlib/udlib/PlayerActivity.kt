package com.udlib.udlib

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import com.actsol.thekee.Episode
import com.actsol.thekee.Subtitle
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Assertions
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.flutter.Log
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PlayerActivity : Activity() {
    companion object {
        val TAG: String = PlayerActivity::class.java.getSimpleName()

        var player: SimpleExoPlayer? = null
        var playerView: PlayerView? = null

        var mediaUrl: String? = ""
        var playPosition: Long? = 0
        var userId: String? = null
        var profileId: String? = null
        var mediaId: String? = null
        var mediaType: String? = null
        var episodePosition: Int? = null
        var episodesList: List<Episode>? = null
        var subtitleList: List<Subtitle>? = null

        var trackSelector: DefaultTrackSelector? = null
        var back_btn: ImageButton? = null
        var audioBtn: ImageButton? = null
        var audioLinear: LinearLayout? = null
        var nextEpisodeLinear: LinearLayout? = null
        var prevEpisodeLinear: LinearLayout? = null
        var ccLienar: LinearLayout? = null
        var audiosList = arrayOf("Auto", "Arabic")
        var ccList = arrayOf("Auto", "Arabic");
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_player)

        mediaUrl = intent.getStringExtra("mediaUrl")
        playPosition = intent.getStringExtra("playPosition")?.toLong()
        userId = intent.getStringExtra("userId")
        profileId = intent.getStringExtra("profileId")
        mediaId = intent.getStringExtra("mediaId")
        mediaType = intent.getStringExtra("mediaType")

        playerView = findViewById(R.id.player_view)
        back_btn = findViewById(R.id.back_btn);
        audioBtn = findViewById(R.id.btn_audio);
        nextEpisodeLinear = findViewById(R.id.next_episode_ll);
        prevEpisodeLinear = findViewById(R.id.prev_episode_ll);
        ccLienar = findViewById(R.id.cc_ll);
        audioLinear = findViewById(R.id.audio_ll);
        audioBtn?.visibility = View.INVISIBLE
        audioLinear?.visibility = View.INVISIBLE
        nextEpisodeLinear?.visibility = View.INVISIBLE
        prevEpisodeLinear?.visibility = View.INVISIBLE
        ccLienar?.visibility = View.INVISIBLE

        back_btn?.setOnClickListener {
            super.onBackPressed()
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
            initializePlayerItems();
        }

        val subtitlesString: String? = intent.getStringExtra("subtitles")
        if (!subtitlesString?.equals("")!!) {
            val gson = GsonBuilder().create()
            subtitleList = gson.fromJson<ArrayList<Subtitle>>(
                subtitlesString,
                object : TypeToken<ArrayList<Subtitle>>() {}.type
            )
        }

        playerInitiate()

    }

    private fun playerInitiate() {
        trackSelector = DefaultTrackSelector(applicationContext)
        trackSelector?.buildUponParameters()
            ?.setMaxVideoSizeSd()
            ?.setPreferredTextLanguage("en")
            ?.setPreferredAudioLanguage("en")

        player = SimpleExoPlayer.Builder(applicationContext)
            .setTrackSelector(trackSelector!!)
            .build()

        playerView?.player = player

        player?.playWhenReady = true

        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(mediaUrl.toString()))
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

    fun initializePlayerItems() {
        if (episodePosition == episodesList?.size?.minus(1) || episodesList!!.isEmpty()) {
            nextEpisodeLinear?.visibility = View.INVISIBLE
        } else {
            nextEpisodeLinear?.visibility = View.VISIBLE
        }

        if (episodePosition!! <= 0 || episodesList!!.isEmpty()) {
            prevEpisodeLinear?.visibility = View.INVISIBLE
        } else {
            prevEpisodeLinear?.visibility = View.VISIBLE
        }


        nextEpisodeLinear?.setOnClickListener {
            updateCurrentTime(
                userId,
                mediaId,
                (player?.getCurrentPosition()!! / 1000).toString(),
                (player?.duration!! / 1000).toString(),
                "ar"
            )
            player?.stop()
            player?.removeListener(PlayerEventListener())
            episodePosition = episodePosition!!.plus(1)
            if (episodesList?.get(episodePosition!!)?.userMediaWatching?.currentTime?.toLong() != null) {
                playPosition =
                    episodesList?.get(episodePosition!!)?.userMediaWatching?.currentTime?.toLong()
            } else {
                playPosition = 0
            }

            mediaUrl = episodesList?.get(episodePosition!!)?.mediaURL
            mediaId = episodesList?.get(episodePosition!!)?.id.toString()
            Log.d(TAG, mediaUrl.toString())
            playerInitiate()
            initializePlayerItems()
        }

        prevEpisodeLinear?.setOnClickListener {
            updateCurrentTime(
                userId,
                mediaId,
                (player?.getCurrentPosition()!! / 1000).toString(),
                (player?.duration!! / 1000).toString(),
                "ar"
            )
            player?.stop()
            player?.removeListener(PlayerEventListener())
            episodePosition = episodePosition!!.minus(1)
            if (episodesList?.get(episodePosition!!)?.userMediaWatching?.currentTime?.toLong() != null) {
                playPosition =
                    episodesList?.get(episodePosition!!)?.userMediaWatching?.currentTime?.toLong()
            } else {
                playPosition = 0
            }
            mediaUrl = episodesList?.get(episodePosition!!)?.mediaURL
            mediaId = episodesList?.get(episodePosition!!)?.id.toString()
            Log.d(TAG, mediaUrl.toString())
            playerInitiate()
            initializePlayerItems()
        }

    }

    fun updateCurrentTime(
        userId: String?,
        mediaId: String?,
        currentTime: String,
        duration: String,
        lang: String
    ) {
        val builder = OkHttpClient.Builder()
        val client = builder.build()


        val api = Retrofit.Builder()
            .baseUrl("https://namaapi.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build().create(ApiService::class.java)

        var fullUrl: String =
            "media/updateCurrentTime?mediaId=$mediaId&time=$currentTime&duration=$duration&userId=$userId&profileId=$profileId&lang=ar"
        Log.d(TAG, "https://namaapi.com/api/v3/$fullUrl")
        val call = api.updateCurrentTime(fullUrl)
        val result = call.enqueue(object : Callback<MediaWatchingItem> {
            override fun onFailure(call: Call<MediaWatchingItem>, t: Throwable) {
                Log.d(TAG, "Call failed! " + t.localizedMessage)
            }

            override fun onResponse(
                call: Call<MediaWatchingItem>,
                response: Response<MediaWatchingItem>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Call OK.")
                } else {
                    Log.d(TAG, "Call error with HTTP status code " + response.code() + "!")
                }

            }

        })
    }

    fun updateCurrentTimeFun(
        userId: String?,
        mediaId: String?,
        currentTime: String,
        duration: String,
        lang: String
    ) {
        val builder = OkHttpClient.Builder()
        val client = builder.build()


        val api = Retrofit.Builder()
            .baseUrl("https://namaapi.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build().create(ApiService::class.java)

        var fullUrl: String =
            "media/updateCurrentTime?mediaId=$mediaId&time=$currentTime&duration=$duration&userId=$userId&profileId=$profileId&lang=ar"
        Log.d(TAG, "https://namaapi.com/api/v3/" + fullUrl)
        val call = api.updateCurrentTime(fullUrl)
        val result = call.enqueue(object : Callback<MediaWatchingItem> {
            override fun onFailure(call: Call<MediaWatchingItem>, t: Throwable) {
                Log.d(TAG, "Call failed! " + t.localizedMessage)
            }

            override fun onResponse(
                call: Call<MediaWatchingItem>,
                response: Response<MediaWatchingItem>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Call OK.")
                } else {
                    Log.d(TAG, "Call error with HTTP status code " + response.code() + "!")
                }

            }

        })
    }

    class PlayerEventListener : Player.Listener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            playerView?.setKeepScreenOn(true)
            if (playWhenReady && playbackState == Player.STATE_READY) { // media actually playing
            } else if (playWhenReady) {

            } else { // player paused in any state
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            Log.d(TAG, "onPlayerError")
        }


        override fun onSeekProcessed() {
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

    override fun onStop() {
        if (player != null && ((player?.getCurrentPosition()!! / 1000) > 0 && (player?.getDuration()!! / 1000) > 0)) {
            updateCurrentTimeFun(
                userId,
                mediaId,
                (player?.getCurrentPosition()!! / 1000).toString(),
                (player?.duration!! / 1000).toString(),
                "ar"
            )
        }
        super.onStop();
        player?.stop()
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
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


}