package com.avalonnarrator.presentation.narrator

import com.avalonnarrator.domain.model.NarratorPreview
import com.avalonnarrator.domain.narration.NarrationPlan
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.playback.PlaybackState

data class NarratorUiState(
    val isInitialized: Boolean = false,
    val config: GameSetupConfig = GameSetupConfig(),
    val narrationPlan: NarrationPlan? = null,
    val playbackState: PlaybackState = PlaybackState(),
    val preview: NarratorPreview = emptyNarratorPreview(),
)

private fun emptyNarratorPreview() = NarratorPreview(
    nowPlayingText = "Not started",
    stepProgressLabel = "0/0",
    estimatedLengthLabel = "--",
    selectedTotal = 0,
    selectedGoodSummary = "None",
    selectedEvilSummary = "None",
    modulesSummary = "None",
    timelineBlocks = emptyList(),
)
