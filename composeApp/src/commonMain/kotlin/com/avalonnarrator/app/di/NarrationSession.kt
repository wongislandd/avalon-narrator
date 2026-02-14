package com.avalonnarrator.app.di

import com.avalonnarrator.domain.narration.NarrationPlan
import com.avalonnarrator.playback.NarrationPlayer
import com.avalonnarrator.playback.PlaybackState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NarrationSession(
    private val narrationPlayer: NarrationPlayer,
) {
    private val _plan = MutableStateFlow<NarrationPlan?>(null)
    val plan: StateFlow<NarrationPlan?> = _plan.asStateFlow()

    val playbackState: StateFlow<PlaybackState> = narrationPlayer.state

    fun prepare(plan: NarrationPlan) {
        _plan.value = plan
        narrationPlayer.load(plan)
    }

    fun playOrPause() {
        if (playbackState.value.isPlaying) narrationPlayer.pause() else narrationPlayer.play()
    }

    fun restart() {
        narrationPlayer.restart()
    }

    fun nextStep() {
        narrationPlayer.nextStep()
    }

    fun pause() {
        narrationPlayer.pause()
    }
}
