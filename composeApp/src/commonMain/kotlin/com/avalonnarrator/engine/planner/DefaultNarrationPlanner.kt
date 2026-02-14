package com.avalonnarrator.engine.planner

import com.avalonnarrator.domain.narration.NarrationPlan
import com.avalonnarrator.domain.narration.PlannedClip
import com.avalonnarrator.domain.narration.PlannedStep
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.domain.setup.RosterBuilder
import com.avalonnarrator.engine.rules.RuleStepDefinition
import com.avalonnarrator.engine.rules.StandardNarrationRules
import kotlin.math.roundToLong

class DefaultNarrationPlanner(
    private val ruleSteps: List<RuleStepDefinition> = StandardNarrationRules.steps,
) : NarrationPlanner {

    override fun plan(config: GameSetupConfig): NarrationPlan {
        val roster = RosterBuilder.build(config)
        val paceMultiplier = config.narrationPace.delayMultiplier
        val steps = ruleSteps
            .filter { it.condition.matches(config, roster.effectiveRoles) }
            .sortedWith(compareBy({ it.phase.sortOrder }, { it.order }))
            .flatMap { step ->
                val postStepDelay = (step.baseDelayMs * paceMultiplier).roundToLong().coerceAtLeast(0)
                val interLineDelay = (step.interClipDelayMs * paceMultiplier).roundToLong().coerceAtLeast(0)
                val lastClipIndex = step.clips.lastIndex
                step.clips.mapIndexed { clipIndex, clipId ->
                    val stepId = if (step.clips.size == 1) {
                        step.id
                    } else {
                        "${step.id}.line${clipIndex + 1}"
                    }
                    PlannedStep(
                        stepId = stepId,
                        phase = step.phase,
                        clips = listOf(PlannedClip(clipId)),
                        delayAfterMs = if (clipIndex == lastClipIndex) postStepDelay else interLineDelay,
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
}
