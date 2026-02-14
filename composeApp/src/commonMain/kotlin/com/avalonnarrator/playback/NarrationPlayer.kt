package com.avalonnarrator.playback

import com.avalonnarrator.domain.narration.NarrationPlan
import kotlinx.coroutines.flow.StateFlow

interface NarrationPlayer {
    val state: StateFlow<PlaybackState>

    fun load(plan: NarrationPlan)
    fun play()
    fun pause()
    fun restart()
    fun nextStep()
}
