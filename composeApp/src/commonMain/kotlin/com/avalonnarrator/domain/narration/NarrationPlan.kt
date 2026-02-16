package com.avalonnarrator.domain.narration

import com.avalonnarrator.domain.audio.ClipId
import com.avalonnarrator.domain.audio.VoicePackId
import com.avalonnarrator.engine.rules.RulePhase

enum class NarrationPauseType {
    STANDARD,
    ACTION,
}

data class PlannedClip(
    val clipId: ClipId,
)

data class PlannedStep(
    val stepId: String,
    val phase: RulePhase,
    val clips: List<PlannedClip>,
    val delayAfterMs: Long,
    val pauseType: NarrationPauseType = NarrationPauseType.STANDARD,
)

data class NarrationPlan(
    val voicePackId: VoicePackId,
    val steps: List<PlannedStep>,
    val totalEstimatedDurationMs: Long,
)
