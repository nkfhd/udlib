package com.udlib.udlib

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
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
import io.flutter.Log
import org.json.JSONObject

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
            // Put the String to pass back into an Intent and close this activity
            val intent = Intent()
            val rootObject = JSONObject()
            var type = if (mediaType.toString().equals("tvshow")) "series" else "movie"
            rootObject.put("mediaId", mediaId.toString())
            rootObject.put("time", (player?.getCurrentPosition()!! / 1000).toString())
            rootObject.put("duration", (player?.duration!! / 1000).toString())
            rootObject.put("userId", userId.toString())
            rootObject.put("profileId", profileId.toString())
            rootObject.put("lang", "ar")
            rootObject.put("type", type.toString())

            intent.putExtra("result_data", rootObject.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
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

    }


}