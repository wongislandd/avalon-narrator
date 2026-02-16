package com.avalonnarrator.playback

import kotlinx.browser.document
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.HTMLAudioElement
import org.w3c.dom.events.Event
import kotlin.coroutines.resume

actual fun createPlatformAudioEngine(): PlatformAudioEngine = WasmPlatformAudioEngine()

private class WasmPlatformAudioEngine : PlatformAudioEngine {
    private var activeAudio: HTMLAudioElement? = null

    override suspend fun play(assetPath: String) {
        val normalizedPath = assetPath.trim().removePrefix("/")
        val audio = document.createElement("audio") as HTMLAudioElement
        audio.src = normalizedPath
        audio.preload = "auto"

        activeAudio?.pause()
        activeAudio = audio

        suspendCancellableCoroutine { continuation ->
            val onComplete: (Event) -> Unit = {
                if (activeAudio == audio) {
                    activeAudio = null
                }
                if (continuation.isActive) {
                    continuation.resume(Unit)
                }
            }

            audio.addEventListener("ended", onComplete)
            audio.addEventListener("error", onComplete)
            audio.play()

            continuation.invokeOnCancellation {
                audio.pause()
                audio.currentTime = 0.0
                if (activeAudio == audio) {
                    activeAudio = null
                }
            }
        }
    }

    override fun pause() {
        activeAudio?.pause()
    }

    override fun stop() {
        activeAudio?.pause()
        activeAudio?.currentTime = 0.0
        activeAudio = null
    }
}
