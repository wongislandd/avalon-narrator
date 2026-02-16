package com.avalonnarrator.playback

interface PlatformAudioEngine {
    suspend fun play(assetPath: String)
    fun pause()
    fun stop()
}

expect fun createPlatformAudioEngine(): PlatformAudioEngine
