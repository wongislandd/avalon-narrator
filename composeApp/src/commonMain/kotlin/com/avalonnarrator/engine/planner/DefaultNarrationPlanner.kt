package com.avalonnarrator.engine.planner

import com.avalonnarrator.domain.audio.ClipId
import com.avalonnarrator.domain.narration.NarrationPlan
import com.avalonnarrator.domain.narration.NarrationPauseType
import com.avalonnarrator.domain.narration.PlannedClip
import com.avalonnarrator.domain.narration.PlannedStep
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.domain.setup.RosterBuilder
import com.avalonnarrator.engine.rules.RulePauseType
import com.avalonnarrator.engine.rules.RuleStepDefinition
import com.avalonnarrator.engine.rules.StandardNarrationRules

class DefaultNarrationPlanner(
    private val ruleSteps: List<RuleStepDefinition> = StandardNarrationRules.steps,
) : NarrationPlanner {

    override fun plan(config: GameSetupConfig): NarrationPlan {
        val roster = RosterBuilder.build(config)
        val steps = ruleSteps
            .filter { it.condition.matches(config, roster.effectiveRoles) }
            .sortedWith(compareBy({ it.phase.sortOrder }, { it.order }))
            .flatMap { step ->
                val lastClipIndex = step.clips.lastIndex
                step.clips.mapIndexed { clipIndex, clipId ->
                    val configuredPauseType = if (clipIndex == lastClipIndex) {
                        step.postStepPauseType
                    } else {
                        step.interClipPauseType
                    }
                    val effectivePauseType = resolveEffectivePauseType(
                        configuredType = configuredPauseType,
                        clipId = clipId,
                    )
                    val stepId = if (step.clips.size == 1) {
                        step.id
                    } else {
                        "${step.id}.line${clipIndex + 1}"
                    }
                    PlannedStep(
                        stepId = stepId,
                        phase = step.phase,
                        clips = listOf(PlannedClip(clipId)),
                        delayAfterMs = resolvePauseMs(effectivePauseType, config),
                        pauseType = mapPauseType(effectivePauseType),
                    )
                }
            }

        val estimatedDuration = steps.sumOf { step ->
            // Base estimate assumes ~1 second spoken content per clip plus configured post-step delay.
            step.clips.size * 1000L + step.delayAfterMs
        }

        return NarrationPlan(
            voicePackId = config.selectedVoicePack,
            steps = steps,
            totalEstimatedDurationMs = estimatedDuration,
        )
    }

    private fun resolvePauseMs(
        type: RulePauseType,
        config: GameSetupConfig,
    ): Long {
        return when (type) {
            RulePauseType.REGULAR -> config.regularPauseMs.toLong()
            RulePauseType.ACTION -> config.actionPauseMs.toLong()
        }.coerceAtLeast(0L)
    }

    private fun mapPauseType(type: RulePauseType): NarrationPauseType = when (type) {
        RulePauseType.REGULAR -> NarrationPauseType.STANDARD
        RulePauseType.ACTION -> NarrationPauseType.ACTION
    }

    private fun resolveEffectivePauseType(
        configuredType: RulePauseType,
        clipId: ClipId,
    ): RulePauseType {
        if (configuredType != RulePauseType.ACTION) {
            return configuredType
        }
        return if (clipNeedsInspectionWindow(clipId)) {
            RulePauseType.ACTION
        } else {
            RulePauseType.REGULAR
        }
    }

    private fun clipNeedsInspectionWindow(clipId: ClipId): Boolean = when (clipId) {
        ClipId.CLERIC_OPEN_EYES,
        ClipId.EVIL_WAKE,
        ClipId.EVIL_WAKE_EXCEPT_EVIL_ROGUE,
        ClipId.MERLIN_WAKE,
        ClipId.PERCIVAL_WAKE,
        ClipId.LANCELOT_WAKE,
        ClipId.SENIOR_MESSENGER_OPEN_EYES,
        ClipId.UNTRUSTWORTHY_OPEN_EYES,
            -> true

        else -> false
    }
}
