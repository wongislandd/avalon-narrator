package com.avalonnarrator.domain.usecase.narration

import com.avalonnarrator.domain.audio.NarrationScriptCatalog
import com.avalonnarrator.domain.model.NarratorPreview
import com.avalonnarrator.domain.model.NarratorRevealSummary
import com.avalonnarrator.domain.model.NarratorTimelineBlock
import com.avalonnarrator.domain.narration.NarrationPlan
import com.avalonnarrator.domain.narration.NarrationPauseType
import com.avalonnarrator.domain.narration.PlannedStep
import com.avalonnarrator.domain.roles.Alignment
import com.avalonnarrator.domain.roles.RoleCatalog
import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.GameModule
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.domain.setup.RosterBuilder
import com.avalonnarrator.playback.PlaybackState

class BuildNarratorPreviewUseCase {

    operator fun invoke(
        config: GameSetupConfig,
        plan: NarrationPlan?,
        playbackState: PlaybackState,
    ): NarratorPreview {
        val roleCounts = buildRoleCounts(config)
        val selectedGoodRoles = roleCounts
            .filter { (roleId, _) -> RoleCatalog.byId(roleId)?.alignment == Alignment.GOOD }
            .toMap()
        val selectedEvilRoles = roleCounts
            .filter { (roleId, _) -> RoleCatalog.byId(roleId)?.alignment == Alignment.EVIL }
            .toMap()
        val selectedTotal = roleCounts.values.sum()
        val steps = plan?.steps.orEmpty()
        val totalSteps = steps.size
        val currentStepNumber = if (playbackState.currentStepIndex >= 0 && totalSteps > 0) {
            (playbackState.currentStepIndex.coerceAtMost(totalSteps - 1)) + 1
        } else {
            0
        }
        return NarratorPreview(
            nowPlayingText = describeCurrentBlock(playbackState = playbackState, steps = steps),
            stepProgressLabel = "$currentStepNumber/$totalSteps",
            estimatedLengthLabel = plan?.totalEstimatedDurationMs?.div(1000)?.toString()?.plus("s") ?: "--",
            selectedTotal = selectedTotal,
            selectedGoodSummary = formatRoleCounts(selectedGoodRoles),
            selectedEvilSummary = formatRoleCounts(selectedEvilRoles),
            modulesSummary = if (config.enabledModules.isEmpty()) {
                "None"
            } else {
                config.enabledModules
                    .sortedBy { it.name }
                    .joinToString(", ") { moduleLabel(it) }
            },
            timelineBlocks = buildTimelineBlocks(steps = steps, roleCounts = roleCounts),
        )
    }

    private fun buildTimelineBlocks(
        steps: List<PlannedStep>,
        roleCounts: Map<RoleId, Int>,
    ): List<NarratorTimelineBlock> = buildList {
        steps.forEachIndexed { stepIndex, step ->
            val canonicalStepId = canonicalStepId(step.stepId)
            add(
                NarratorTimelineBlock.Info(
                    stepIndex = stepIndex,
                    phaseLabel = step.phase.name,
                    stepLabel = stepLabel(canonicalStepId),
                    lines = step.clips.map { NarrationScriptCatalog.lineFor(it.clipId) },
                    revealSummary = buildRevealPreview(
                        stepId = canonicalStepId,
                        roleCounts = roleCounts,
                    ),
                ),
            )
            if (step.delayAfterMs > 0L) {
                add(
                    NarratorTimelineBlock.Pause(
                        stepIndex = stepIndex,
                        pauseMs = step.delayAfterMs,
                        pauseType = step.pauseType,
                    ),
                )
            }
        }
    }

    private fun describeCurrentBlock(
        playbackState: PlaybackState,
        steps: List<PlannedStep>,
    ): String {
        val currentStep = steps.getOrNull(playbackState.currentStepIndex) ?: return "Not started"
        if (playbackState.isInDelay) {
            return "${pauseTypeLabel(currentStep.pauseType)} after ${stepLabel(canonicalStepId(currentStep.stepId))}"
        }

        val currentClip = currentStep.clips.getOrNull(playbackState.currentClipIndex)
        return if (currentClip != null) {
            "${NarrationScriptCatalog.lineFor(currentClip.clipId)} (${stepLabel(canonicalStepId(currentStep.stepId))})"
        } else {
            "Preparing ${stepLabel(canonicalStepId(currentStep.stepId))}"
        }
    }

    private fun buildRoleCounts(config: GameSetupConfig): Map<RoleId, Int> {
        val roster = RosterBuilder.build(config)
        val counts = mutableMapOf<RoleId, Int>()
        roster.selectedSpecialRoles.forEach { counts[it] = 1 }
        if (roster.loyalServantCount > 0) {
            counts[RoleId.LOYAL_SERVANT] = roster.loyalServantCount
        }
        if (roster.minionCount > 0) {
            counts[RoleId.MINION] = roster.minionCount
        }
        return counts
    }

    private fun buildRevealPreview(
        stepId: String,
        roleCounts: Map<RoleId, Int>,
    ): NarratorRevealSummary {
        val evilRoles = roleCounts.filterKeys { roleId ->
            RoleCatalog.byId(roleId)?.alignment == Alignment.EVIL
        }
        val evilVisibleToEvil = evilRoles.filterKeys { it != RoleId.ROGUE_EVIL }
        val evilVisibleToMerlin = evilRoles.filterKeys { it != RoleId.MORDRED && it != RoleId.SORCERER_EVIL }
        val percivalView = roleCounts.filterKeys { it == RoleId.MERLIN || it == RoleId.MORGANA }
        val lancelotPair = roleCounts.filterKeys { it == RoleId.LANCELOT_GOOD || it == RoleId.LANCELOT_EVIL }

        return when (stepId) {
            "intro" -> NarratorRevealSummary(
                audienceLabel = "All players",
                revealedRoles = emptyMap(),
                note = "Opening instruction before role reveals.",
            )

            "cleric_alignment_check" -> NarratorRevealSummary(
                audienceLabel = "Cleric",
                revealedRoles = emptyMap(),
                note = "Cleric learns whether the current leader is good or evil.",
            )

            "evil_info" -> NarratorRevealSummary(
                audienceLabel = "Evil team",
                revealedRoles = evilVisibleToEvil,
                note = if (RoleId.ROGUE_EVIL in evilRoles) {
                    "Evil Rogue stays hidden from the rest of evil. Oberon behavior depends on table rules."
                } else {
                    "Oberon behavior depends on table rules."
                },
            )

            "merlin_info" -> NarratorRevealSummary(
                audienceLabel = "Merlin",
                revealedRoles = evilVisibleToMerlin,
                note = if (RoleId.SORCERER_EVIL in evilRoles) {
                    "Mordred and Evil Sorcerer remain hidden from Merlin."
                } else {
                    "Mordred remains hidden from Merlin."
                },
            )

            "percival_info_pair" -> NarratorRevealSummary(
                audienceLabel = "Percival",
                revealedRoles = percivalView,
                note = "Percival sees both Merlin and Morgana.",
            )

            "percival_info_merlin_only" -> NarratorRevealSummary(
                audienceLabel = "Percival",
                revealedRoles = roleCounts.filterKeys { it == RoleId.MERLIN },
                note = "Percival gets only Merlin information in this setup.",
            )

            "lancelot_counterpart" -> NarratorRevealSummary(
                audienceLabel = "Lancelot roles",
                revealedRoles = lancelotPair,
                note = "Lancelot module wake/close sequence is inferred from selected Lancelot roles.",
            )

            "messenger_pair_info" -> NarratorRevealSummary(
                audienceLabel = "Senior Messenger",
                revealedRoles = roleCounts.filterKeys { it == RoleId.JUNIOR_MESSENGER },
                note = "Senior Messenger sees Junior Messenger; Evil Messenger remains hidden in this reveal.",
            )

            "untrustworthy_servant_info" -> NarratorRevealSummary(
                audienceLabel = "Untrustworthy Servant",
                revealedRoles = roleCounts.filterKeys { it == RoleId.ASSASSIN },
                note = "Untrustworthy Servant identifies Assassin.",
            )

            "lady_module", "lady_module_pass" -> NarratorRevealSummary(
                audienceLabel = "Table reminder",
                revealedRoles = emptyMap(),
                note = "Lady of the Lake procedure reminder.",
            )

            "excalibur_module", "excalibur_switch" -> NarratorRevealSummary(
                audienceLabel = "Table reminder",
                revealedRoles = emptyMap(),
                note = "Excalibur procedure reminder.",
            )

            "closing" -> NarratorRevealSummary(
                audienceLabel = "All players",
                revealedRoles = emptyMap(),
                note = "Everyone opens eyes and game begins.",
            )

            else -> {
                if (stepId.startsWith("reminder_")) {
                    NarratorRevealSummary(
                        audienceLabel = "All players",
                        revealedRoles = emptyMap(),
                        note = "Role reminder for selected setup.",
                    )
                } else {
                    NarratorRevealSummary(
                        audienceLabel = "Unknown",
                        revealedRoles = emptyMap(),
                        note = "",
                    )
                }
            }
        }
    }

    private fun formatRoleCounts(roleCounts: Map<RoleId, Int>): String {
        if (roleCounts.isEmpty()) return "None"
        return roleCounts.entries
            .sortedBy { roleName(it.key) }
            .joinToString(", ") { (roleId, count) ->
                val suffix = if (count > 1) " x$count" else ""
                "${roleName(roleId)}$suffix"
            }
    }

    private fun roleName(roleId: RoleId): String = RoleCatalog.byId(roleId)?.name ?: roleId.name

    private fun stepLabel(stepId: String): String = stepId
        .split('_')
        .joinToString(" ") { token -> token.lowercase().replaceFirstChar(Char::titlecase) }

    private fun canonicalStepId(stepId: String): String = stepId.substringBefore(".line")

    private fun moduleLabel(module: GameModule): String = module.name
        .split('_')
        .joinToString(" ") { token -> token.lowercase().replaceFirstChar(Char::titlecase) }

    private fun pauseTypeLabel(type: NarrationPauseType): String = when (type) {
        NarrationPauseType.ACTION -> "Action Pause"
        NarrationPauseType.STANDARD -> "Standard Pause"
    }
}
