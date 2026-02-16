package com.avalonnarrator.domain.model

import com.avalonnarrator.domain.narration.NarrationPauseType
import com.avalonnarrator.domain.roles.RoleId

data class NarratorPreview(
    val nowPlayingText: String,
    val stepProgressLabel: String,
    val estimatedLengthLabel: String,
    val selectedTotal: Int,
    val selectedGoodSummary: String,
    val selectedEvilSummary: String,
    val modulesSummary: String,
    val timelineBlocks: List<NarratorTimelineBlock>,
)

sealed interface NarratorTimelineBlock {
    val stepIndex: Int

    data class Info(
        override val stepIndex: Int,
        val phaseLabel: String,
        val stepLabel: String,
        val lines: List<String>,
        val revealSummary: NarratorRevealSummary,
    ) : NarratorTimelineBlock

    data class Pause(
        override val stepIndex: Int,
        val pauseMs: Long,
        val pauseType: NarrationPauseType,
    ) : NarratorTimelineBlock
}

data class NarratorRevealSummary(
    val audienceLabel: String,
    val revealedRoles: Map<RoleId, Int>,
    val note: String,
)
