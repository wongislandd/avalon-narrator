package com.avalonnarrator.playback

interface PlatformAudioEngine {
    suspend fun play(assetPath: String)
    fun pause()
    fun stop()
    fun startBacktrack(assetPath: String, volume: Float = 0.22f) {}
    fun stopBacktrack() {}
}

expect fun createPlatformAudioEngine(): PlatformAudioEngine
