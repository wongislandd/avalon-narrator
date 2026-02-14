package com.avalonnarrator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avalonnarrator.app.AvalonUiState
import com.avalonnarrator.domain.audio.NarrationScriptCatalog
import com.avalonnarrator.domain.narration.PlannedStep
import com.avalonnarrator.domain.roles.Alignment
import com.avalonnarrator.domain.roles.RoleCatalog
import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.GameModule
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.domain.setup.RosterBuilder
import com.avalonnarrator.playback.PlaybackState

@Composable
fun NarratorScreen(
    uiState: AvalonUiState,
    playbackState: PlaybackState,
    onPlayPause: () -> Unit,
    onRestart: () -> Unit,
) {
    val plan = uiState.narrationPlan
    val roleCounts = buildRoleCounts(uiState.config)
    val selectedGoodRoles = roleCounts
        .filter { (roleId, _) -> RoleCatalog.byId(roleId)?.alignment == Alignment.GOOD }
        .toMap()
    val selectedEvilRoles = roleCounts
        .filter { (roleId, _) -> RoleCatalog.byId(roleId)?.alignment == Alignment.EVIL }
        .toMap()
    val selectedTotal = roleCounts.values.sum()
    val timelineBlocks = buildTimelineBlocks(plan?.steps.orEmpty())
    val currentBlockLabel = describeCurrentBlock(playbackState, plan?.steps.orEmpty())
    val estimatedLength = plan?.totalEstimatedDurationMs?.div(1000)?.toString()?.plus("s") ?: "--"
    val modulesLabel = if (uiState.config.enabledModules.isEmpty()) {
        "None"
    } else {
        uiState.config.enabledModules
            .sortedBy { it.name }
            .joinToString(", ") { moduleLabel(it) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF181007), Color(0xFF3C2916), Color(0xFF6A4D2A)),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 8.dp),
            ) {
                item {
                    Text(
                        text = "Narrator's Chamber",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFEBC0),
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Guide the table through the night phase.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE2CFAB),
                    )
                }

                item {
                    Surface(
                        color = Color(0x6620140B),
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0x66D5AA62), MaterialTheme.shapes.large),
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                "Voice Pack: ${uiState.config.selectedVoicePack}",
                                color = Color(0xFFFFEBC0),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text("Estimated Length: $estimatedLength", color = Color(0xFFF4DFC1))
                            Text("Selected Characters: $selectedTotal", color = Color(0xFFF4DFC1))
                        }
                    }
                }

                item {
                    val playbackBorder = if (playbackState.isPlaying) Color(0xFFE0C06F) else Color(0x66D5AA62)
                    val playbackBackground = if (playbackState.isPlaying) Color(0x7F2B2215) else Color(0x6620140B)
                    Surface(
                        color = playbackBackground,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, playbackBorder, MaterialTheme.shapes.large),
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                "Now Playing",
                                color = Color(0xFFFFEBC0),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(currentBlockLabel, color = Color(0xFFF8E8CC))
                            Text("Step ${playbackState.currentStepIndex}", color = Color(0xFFD9C39D))
                        }
                    }
                }

                item {
                    Surface(
                        color = Color(0x4CFFE6B8),
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "Selection Preview",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2A1B10),
                            )
                            Text("Good: ${formatRoleCounts(selectedGoodRoles)}", color = Color(0xFF2A1B10))
                            Text("Evil: ${formatRoleCounts(selectedEvilRoles)}", color = Color(0xFF2A1B10))
                            Text("Modules: $modulesLabel", color = Color(0xFF2A1B10))
                        }
                    }
                }

                item {
                    Text(
                        "Playback Timeline",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFEBC0),
                    )
                }

                itemsIndexed(timelineBlocks) { _, block ->
                    when (block) {
                        is TimelineBlock.Info -> {
                            val active = block.stepIndex == playbackState.currentStepIndex && !playbackState.isInDelay
                            val background = if (active) Color(0x80325366) else Color(0x6620140B)
                            val border = if (active) Color(0xFFE6C374) else Color(0x668D6A35)
                            val revealPreview = buildRevealPreview(
                                stepId = canonicalStepId(block.step.stepId),
                                roleCounts = roleCounts,
                            )
                            Surface(
                                color = background,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, border, MaterialTheme.shapes.medium),
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    Text(
                                        "${block.step.phase}: ${stepLabel(canonicalStepId(block.step.stepId))}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFEBC0),
                                    )
                                    Text("Audience: ${revealPreview.audienceLabel}", color = Color(0xFFF4DFC1))
                                    if (revealPreview.revealedRoles.isNotEmpty()) {
                                        Text("Revealed: ${formatRoleCounts(revealPreview.revealedRoles)}", color = Color(0xFFF4DFC1))
                                    }
                                    if (revealPreview.note.isNotBlank()) {
                                        Text("Note: ${revealPreview.note}", color = Color(0xFFE0CBAB))
                                    }
                                    Text("Lines:", fontWeight = FontWeight.SemiBold, color = Color(0xFFFFEBC0))
                                    block.step.clips.forEach { clip ->
                                        Text("• ${NarrationScriptCatalog.lineFor(clip.clipId)}", color = Color(0xFFF3DFBF))
                                    }
                                }
                            }
                        }

                        is TimelineBlock.Delay -> {
                            val active = block.stepIndex == playbackState.currentStepIndex && playbackState.isInDelay
                            val background = if (active) Color(0x99A66E2D) else Color(0x663B2A16)
                            val border = if (active) Color(0xFFFFDF9A) else Color(0x668D6A35)
                            Surface(
                                color = background,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, border, MaterialTheme.shapes.medium),
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    Text("Delay Block", fontWeight = FontWeight.Bold, color = Color(0xFFFFF2D8))
                                    Text("${block.delayMs} ms pause before next info.", color = Color(0xFFFFE8C4))
                                }
                            }
                        }
                    }
                }

                if (uiState.config.debugTimelineEnabled && playbackState.debugMessages.isNotEmpty()) {
                    item {
                        Text("Debug", style = MaterialTheme.typography.titleMedium, color = Color(0xFFFFEBC0))
                    }
                    itemsIndexed(playbackState.debugMessages) { _, message ->
                        Text("• $message", color = Color(0xFFE8D1AF))
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = onPlayPause,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9C7A35),
                        contentColor = Color(0xFFFFF3D6),
                    ),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(if (playbackState.isPlaying) "Pause" else "Play", fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = onRestart,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Restart", color = Color(0xFFFFEBC0), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

private sealed interface TimelineBlock {
    val stepIndex: Int

    data class Info(
        override val stepIndex: Int,
        val step: PlannedStep,
    ) : TimelineBlock

    data class Delay(
        override val stepIndex: Int,
        val delayMs: Long,
    ) : TimelineBlock
}

private data class RevealPreview(
    val audienceLabel: String,
    val revealedRoles: Map<RoleId, Int>,
    val note: String,
)

private fun buildRevealPreview(
    stepId: String,
    roleCounts: Map<RoleId, Int>,
): RevealPreview {
    val evilRoles = roleCounts.filterKeys { roleId ->
        RoleCatalog.byId(roleId)?.alignment == Alignment.EVIL
    }
    val evilVisibleToEvil = evilRoles.filterKeys { it != RoleId.ROGUE_EVIL }
    val evilVisibleToMerlin = evilRoles.filterKeys { it != RoleId.MORDRED && it != RoleId.SORCERER_EVIL }
    val percivalView = roleCounts.filterKeys { it == RoleId.MERLIN || it == RoleId.MORGANA }
    val lancelotPair = roleCounts.filterKeys { it == RoleId.LANCELOT_GOOD || it == RoleId.LANCELOT_EVIL }

    return when (stepId) {
        "intro" -> RevealPreview(
            audienceLabel = "All players",
            revealedRoles = emptyMap(),
            note = "Opening instruction before role reveals.",
        )

        "cleric_alignment_check" -> RevealPreview(
            audienceLabel = "Cleric",
            revealedRoles = emptyMap(),
            note = "Cleric learns whether the current leader is good or evil.",
        )

        "evil_info" -> RevealPreview(
            audienceLabel = "Evil team",
            revealedRoles = evilVisibleToEvil,
            note = if (RoleId.ROGUE_EVIL in evilRoles) {
                "Evil Rogue stays hidden from the rest of evil. Oberon behavior depends on table rules."
            } else {
                "Oberon behavior depends on table rules."
            },
        )

        "merlin_info" -> RevealPreview(
            audienceLabel = "Merlin",
            revealedRoles = evilVisibleToMerlin,
            note = if (RoleId.SORCERER_EVIL in evilRoles) {
                "Mordred and Evil Sorcerer remain hidden from Merlin."
            } else {
                "Mordred remains hidden from Merlin."
            },
        )

        "percival_info_pair" -> RevealPreview(
            audienceLabel = "Percival",
            revealedRoles = percivalView,
            note = "Percival sees both Merlin and Morgana.",
        )

        "percival_info_merlin_only" -> RevealPreview(
            audienceLabel = "Percival",
            revealedRoles = roleCounts.filterKeys { it == RoleId.MERLIN },
            note = "Percival gets only Merlin information in this setup.",
        )

        "lancelot_counterpart" -> RevealPreview(
            audienceLabel = "Lancelot roles",
            revealedRoles = lancelotPair,
            note = "Lancelot module wake/close sequence is inferred from selected Lancelot roles.",
        )

        "messenger_pair_info" -> RevealPreview(
            audienceLabel = "Senior Messenger",
            revealedRoles = roleCounts.filterKeys { it == RoleId.JUNIOR_MESSENGER },
            note = "Senior Messenger sees Junior Messenger; Evil Messenger remains hidden in this reveal.",
        )

        "untrustworthy_servant_info" -> RevealPreview(
            audienceLabel = "Untrustworthy Servant",
            revealedRoles = roleCounts.filterKeys { it == RoleId.ASSASSIN },
            note = "Untrustworthy Servant identifies Assassin.",
        )

        "lady_module", "lady_module_pass" -> RevealPreview(
            audienceLabel = "Table reminder",
            revealedRoles = emptyMap(),
            note = "Lady of the Lake procedure reminder.",
        )

        "excalibur_module", "excalibur_switch" -> RevealPreview(
            audienceLabel = "Table reminder",
            revealedRoles = emptyMap(),
            note = "Excalibur procedure reminder.",
        )

        "closing" -> RevealPreview(
            audienceLabel = "All players",
            revealedRoles = emptyMap(),
            note = "Everyone opens eyes and game begins.",
        )

        else -> {
            if (stepId.startsWith("reminder_")) {
                RevealPreview(
                    audienceLabel = "All players",
                    revealedRoles = emptyMap(),
                    note = "Role reminder for selected setup.",
                )
            } else {
                RevealPreview(
                    audienceLabel = "Unknown",
                    revealedRoles = emptyMap(),
                    note = "",
                )
            }
        }
    }
}

private fun buildTimelineBlocks(steps: List<PlannedStep>): List<TimelineBlock> = buildList {
    steps.forEachIndexed { stepIndex, step ->
        add(TimelineBlock.Info(stepIndex = stepIndex, step = step))
        if (step.delayAfterMs > 0L) {
            add(TimelineBlock.Delay(stepIndex = stepIndex, delayMs = step.delayAfterMs))
        }
    }
}

private fun describeCurrentBlock(
    playbackState: PlaybackState,
    steps: List<PlannedStep>,
): String {
    val currentStep = steps.getOrNull(playbackState.currentStepIndex) ?: return "Not started"
    if (playbackState.isInDelay) {
        return "Delay after ${stepLabel(canonicalStepId(currentStep.stepId))}"
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
