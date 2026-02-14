package com.avalonnarrator.playback

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual fun createPlatformAudioEngine(): PlatformAudioEngine =
    AndroidPlatformAudioEngine(AndroidAppContext.requireContext())

private class AndroidPlatformAudioEngine(
    context: android.content.Context,
) : PlatformAudioEngine {
    private val narrationPlayer = ExoPlayer.Builder(context).build()
    private val backtrackPlayer = ExoPlayer.Builder(context).build()
    private var backtrackListener: Player.Listener? = null

    override suspend fun play(assetPath: String) {
        val uri = "asset:///$assetPath"
        suspendCancellableCoroutine<Unit> { continuation ->
            var settled = false
            val listener = object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED && !settled) {
                        settled = true
                        narrationPlayer.removeListener(this)
                        continuation.resume(Unit)
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    if (!settled) {
                        settled = true
                        narrationPlayer.removeListener(this)
                        continuation.resume(Unit)
                    }
                }
            }

            narrationPlayer.addListener(listener)
            narrationPlayer.setMediaItem(MediaItem.fromUri(uri))
            narrationPlayer.prepare()
            narrationPlayer.playWhenReady = true

            continuation.invokeOnCancellation {
                if (!settled) {
                    settled = true
                    narrationPlayer.removeListener(listener)
                    narrationPlayer.stop()
                }
            }
        }
    }

    override fun startBacktrack(assetPath: String, volume: Float) {
        stopBacktrack()
        val uri = "asset:///$assetPath"

        val listener = object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                stopBacktrack()
            }
        }
        backtrackListener = listener
        backtrackPlayer.addListener(listener)
        backtrackPlayer.volume = volume.coerceIn(0f, 1f)
        backtrackPlayer.repeatMode = Player.REPEAT_MODE_ONE
        backtrackPlayer.setMediaItem(MediaItem.fromUri(uri))
        backtrackPlayer.prepare()
        backtrackPlayer.playWhenReady = true
    }

    override fun stopBacktrack() {
        backtrackListener?.let {
            backtrackPlayer.removeListener(it)
            backtrackListener = null
        }
        backtrackPlayer.stop()
        backtrackPlayer.clearMediaItems()
    }

    override fun pause() {
        narrationPlayer.pause()
        backtrackPlayer.pause()
    }

    override fun stop() {
        narrationPlayer.stop()
        narrationPlayer.clearMediaItems()
        stopBacktrack()
    }
}
