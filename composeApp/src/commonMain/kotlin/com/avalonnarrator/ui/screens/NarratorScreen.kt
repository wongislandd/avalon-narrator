package com.avalonnarrator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.avalonnarrator.domain.model.NarratorTimelineBlock
import com.avalonnarrator.presentation.narrator.NarratorUiEvent
import com.avalonnarrator.presentation.narrator.NarratorUiState

@Composable
fun NarratorScreen(
    uiState: NarratorUiState,
    onEvent: (NarratorUiEvent) -> Unit,
) {
    val playbackState = uiState.playbackState
    val preview = uiState.preview

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
                            Text("Estimated Length: ${preview.estimatedLengthLabel}", color = Color(0xFFF4DFC1))
                            Text("Selected Characters: ${preview.selectedTotal}", color = Color(0xFFF4DFC1))
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
                            Text(preview.nowPlayingText, color = Color(0xFFF8E8CC))
                            Text("Step ${preview.stepProgressLabel}", color = Color(0xFFD9C39D))
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
                            Text("Good: ${preview.selectedGoodSummary}", color = Color(0xFF2A1B10))
                            Text("Evil: ${preview.selectedEvilSummary}", color = Color(0xFF2A1B10))
                            Text("Modules: ${preview.modulesSummary}", color = Color(0xFF2A1B10))
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

                itemsIndexed(preview.timelineBlocks) { _, block ->
                    when (block) {
                        is NarratorTimelineBlock.Info -> {
                            val active = block.stepIndex == playbackState.currentStepIndex && !playbackState.isInDelay
                            val background = if (active) Color(0x80325366) else Color(0x6620140B)
                            val border = if (active) Color(0xFFE6C374) else Color(0x668D6A35)
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
                                        "${block.phaseLabel}: ${block.stepLabel}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFEBC0),
                                    )
                                    Text("Audience: ${block.revealSummary.audienceLabel}", color = Color(0xFFF4DFC1))
                                    if (block.revealSummary.revealedRoles.isNotEmpty()) {
                                        val revealed = block.revealSummary.revealedRoles.entries.joinToString(", ") { (role, count) ->
                                            "${role.name.lowercase().replace('_', ' ').replaceFirstChar(Char::titlecase)}${if (count > 1) " x$count" else ""}"
                                        }
                                        Text("Revealed: $revealed", color = Color(0xFFF4DFC1))
                                    }
                                    if (block.revealSummary.note.isNotBlank()) {
                                        Text("Note: ${block.revealSummary.note}", color = Color(0xFFE0CBAB))
                                    }
                                    Text("Lines:", fontWeight = FontWeight.SemiBold, color = Color(0xFFFFEBC0))
                                    block.lines.forEach { line ->
                                        Text("• $line", color = Color(0xFFF3DFBF))
                                    }
                                }
                            }
                        }

                        is NarratorTimelineBlock.Delay -> {
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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = { onEvent(NarratorUiEvent.PlayPause) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9C7A35),
                        contentColor = Color(0xFFFFF3D6),
                    ),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(if (playbackState.isPlaying) "Pause" else "Play", fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = { onEvent(NarratorUiEvent.NextStep) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Next", color = Color(0xFFFFEBC0), fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = { onEvent(NarratorUiEvent.Restart) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Restart", color = Color(0xFFFFEBC0), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
