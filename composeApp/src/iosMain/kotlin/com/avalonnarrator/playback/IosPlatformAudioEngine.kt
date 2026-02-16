package com.avalonnarrator.playback

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioPlayerDelegateProtocol
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.Foundation.NSBundle
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.darwin.NSObject
import kotlin.coroutines.resume

actual fun createPlatformAudioEngine(): PlatformAudioEngine = IosPlatformAudioEngine()

@OptIn(ExperimentalForeignApi::class)
private class IosPlatformAudioEngine : PlatformAudioEngine {
    private var activePlayer: AVAudioPlayer? = null
    private var activeDelegate: AVAudioPlayerDelegateProtocol? = null

    override suspend fun play(assetPath: String) {
        val resourcePath = resolveBundlePath(assetPath) ?: run {
            return
        }

        ensurePlaybackSession()

        suspendCancellableCoroutine<Unit> { continuation ->
            val url = NSURL.fileURLWithPath(resourcePath)
            val player = AVAudioPlayer(contentsOfURL = url, error = null)

            val delegate = CompletionDelegate {
                if (activePlayer == player) {
                    activePlayer = null
                    activeDelegate = null
                }
                if (continuation.isActive) {
                    continuation.resume(Unit)
                }
            }

            activePlayer = player
            activeDelegate = delegate
            player.delegate = delegate
            player.prepareToPlay()
            val started = player.play()
            if (!started && continuation.isActive) {
                continuation.resume(Unit)
            }

            continuation.invokeOnCancellation {
                player.stop()
                if (activePlayer == player) {
                    activePlayer = null
                    activeDelegate = null
                }
            }
        }
    }

    override fun pause() {
        activePlayer?.pause()
    }

    override fun stop() {
        activePlayer?.stop()
        activePlayer = null
        activeDelegate = null
    }

    private fun resolveBundlePath(assetPath: String): String? {
        val normalized = assetPath.trim().removePrefix("/")
        val candidates = listOf(
            normalized,
            "compose-resources/$normalized",
            "composeResources/$normalized",
        )
        candidates.forEach { candidate ->
            val fromLookup = resolvePathInMainBundle(candidate)
            if (fromLookup != null) return fromLookup
        }
        return null
    }

    private fun resolvePathInMainBundle(relativePath: String): String? {
        val lastSlash = relativePath.lastIndexOf('/')
        val directory = if (lastSlash >= 0) relativePath.substring(0, lastSlash) else null
        val fileName = if (lastSlash >= 0) relativePath.substring(lastSlash + 1) else relativePath
        val dot = fileName.lastIndexOf('.')
        val resourceName = if (dot > 0) fileName.substring(0, dot) else fileName
        val extension = if (dot > 0) fileName.substring(dot + 1) else null

        val fromLookup = NSBundle.mainBundle.pathForResource(resourceName, extension, directory)
        if (fromLookup != null) return fromLookup

        val directPath = NSBundle.mainBundle.resourcePath?.let { "$it/$relativePath" }
        if (directPath != null && NSFileManager.defaultManager.fileExistsAtPath(directPath)) {
            return directPath
        }
        return null
    }

    private fun ensurePlaybackSession() {
        AVAudioSession.sharedInstance().setCategory(AVAudioSessionCategoryPlayback, null)
    }
}

@OptIn(ExperimentalForeignApi::class)
private class CompletionDelegate(
    private val onComplete: () -> Unit,
) : NSObject(), AVAudioPlayerDelegateProtocol {
    override fun audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully: Boolean) {
        onComplete()
    }

    override fun audioPlayerDecodeErrorDidOccur(player: AVAudioPlayer, error: NSError?) {
        onComplete()
    }
}
