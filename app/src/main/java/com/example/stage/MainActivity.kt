package com.example.stage

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsManifest
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerControlView
import androidx.media3.ui.PlayerView
import com.example.stage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var playerView: PlayerView? = null
    private var player: ExoPlayer? = null

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playerView = binding.playerView
        setUpPlayer()
        addManifest()
    }

    private fun addManifest() {
        player?.addListener(
            object : Player.Listener {
                @OptIn(UnstableApi::class) override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                    super.onTimelineChanged(timeline, reason)
                    val manifest = player?.currentManifest
                    if(manifest is HlsManifest) {
                        Log.i("addManifest", "$manifest")
                    }
                }

                override fun onTracksChanged(tracks: Tracks) {
                    super.onTracksChanged(tracks)
                    for (trackGroup in tracks.groups) {
                        // Group level information.
                        val trackType = trackGroup.type
                        val trackInGroupIsSelected = trackGroup.isSelected
                        val trackInGroupIsSupported = trackGroup.isSupported
                        for (i in 0 until trackGroup.length) {
                            // Individual track information.
                            val isSupported = trackGroup.isTrackSupported(i)
                            val isSelected = trackGroup.isTrackSelected(i)
                            val trackFormat = trackGroup.getTrackFormat(i)
                            Log.i("onTracksChanged", "$isSupported $isSelected $trackFormat")
                        }
                    }
                }
            }
        )
    }

    @OptIn(UnstableApi::class)
    private fun setUpPlayer() {
        // Initializing exoplayer

        // Create a player instance.
        player = ExoPlayer.Builder(baseContext).build()
        playerView?.player = player
        playVideo()
    }

    @OptIn(UnstableApi::class) private fun playVideo() {
        val dataSourceFactory: DefaultDataSource.Factory = DefaultDataSource.Factory(baseContext)
        val hlsMediaSource: HlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri("https://live-par-2-cdn-alt.livepush.io/live/bigbuckbunnyclip/index.m3u8"))
        player?.setMediaSource(hlsMediaSource)
        player?.prepare()
        player?.play()
    }

    @OptIn(UnstableApi::class)
    override fun onResume() {
        super.onResume()
        playerView?.player?.play()
    }

    @OptIn(UnstableApi::class)
    override fun onPause() {
        super.onPause()
        playerView?.player?.pause()
    }

    @OptIn(UnstableApi::class)
    override fun onDestroy() {
        super.onDestroy()
        playerView?.player?.stop()
        playerView?.player?.release()
    }

}