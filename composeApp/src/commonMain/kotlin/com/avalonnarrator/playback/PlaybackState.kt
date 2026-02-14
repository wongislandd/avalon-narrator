package com.avalonnarrator.playback

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentStepIndex: Int = -1,
    val currentClipIndex: Int = -1,
    val isInDelay: Boolean = false,
    val debugMessages: List<String> = emptyList(),
)
