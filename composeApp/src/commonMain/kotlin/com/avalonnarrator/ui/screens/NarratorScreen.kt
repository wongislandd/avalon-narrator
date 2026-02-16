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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avalonnarrator.domain.model.NarratorTimelineBlock
import com.avalonnarrator.domain.narration.NarrationPauseType
import com.avalonnarrator.presentation.narrator.NarratorUiEvent
import com.avalonnarrator.presentation.narrator.NarratorUiState

@Composable
fun NarratorScreen(
    uiState: NarratorUiState,
    onEvent: (NarratorUiEvent) -> Unit,
) {
    val playbackState = uiState.playbackState
    val preview = uiState.preview
    val timelineListState = rememberLazyListState()
    val activeBlockIndex = preview.timelineBlocks.indexOfFirst { block ->
        when (block) {
            is NarratorTimelineBlock.Info -> {
                block.stepIndex == playbackState.currentStepIndex && !playbackState.isInDelay
            }

            is NarratorTimelineBlock.Pause -> {
                block.stepIndex == playbackState.currentStepIndex && playbackState.isInDelay
            }
        }
    }

    LaunchedEffect(activeBlockIndex, preview.timelineBlocks.size) {
        if (activeBlockIndex >= 0) {
            timelineListState.animateScrollToItem(activeBlockIndex)
        }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { onEvent(NarratorUiEvent.Back) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFFFEBC0),
                    )
                }
                Column {
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
            }
            Spacer(Modifier.height(12.dp))

            NarratorSessionSummaryCard(
                voicePack = uiState.config.selectedVoicePack,
                estimatedLength = preview.estimatedLengthLabel,
                selectedTotal = preview.selectedTotal,
                stepProgress = preview.stepProgressLabel,
                selectedGoodSummary = preview.selectedGoodSummary,
                selectedEvilSummary = preview.selectedEvilSummary,
                modulesSummary = preview.modulesSummary,
            )

            Spacer(Modifier.height(14.dp))
            Text(
                "Playback Timeline",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFEBC0),
            )
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = timelineListState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 8.dp),
            ) {
                itemsIndexed(preview.timelineBlocks) { _, block ->
                    when (block) {
                        is NarratorTimelineBlock.Info -> {
                            val active = block.stepIndex == playbackState.currentStepIndex && !playbackState.isInDelay
                            val background = if (active) Color(0x80325366) else Color(0x6620140B)
                            val border = if (active) Color(0xFFE6C374) else Color(0x668D6A35)
                            val spokenLine = block.lines.firstOrNull().orEmpty()
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
                                        text = spokenLine.ifBlank { block.stepLabel },
                                        color = if (active) Color(0xFFFFF2D8) else Color(0xFFF3DFBF),
                                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                                    )
                                }
                            }
                        }

                        is NarratorTimelineBlock.Pause -> {
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
                                    Text(
                                        "${pauseTypeLabel(block.pauseType)}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFF2D8),
                                    )
                                    Text("${block.pauseMs} ms", color = Color(0xFFFFE8C4))
                                }
                            }
                        }
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
                    Icon(
                        imageVector = if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                    )
                }
                OutlinedButton(
                    onClick = { onEvent(NarratorUiEvent.Restart) },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Replay,
                        contentDescription = "Restart",
                        tint = Color(0xFFFFEBC0),
                    )
                }
            }
        }
    }
}

@Composable
private fun NarratorSessionSummaryCard(
    voicePack: String,
    estimatedLength: String,
    selectedTotal: Int,
    stepProgress: String,
    selectedGoodSummary: String,
    selectedEvilSummary: String,
    modulesSummary: String,
) {
    Surface(
        color = Color(0x6620140B),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0x66D5AA62), MaterialTheme.shapes.large),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                "Voice Pack: $voicePack",
                color = Color(0xFFFFEBC0),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text("Estimated Length: $estimatedLength", color = Color(0xFFF4DFC1))
            Text("Selected Characters: $selectedTotal", color = Color(0xFFF4DFC1))
            Text("Step $stepProgress", color = Color(0xFFD9C39D))
            Spacer(Modifier.height(2.dp))
            Text(
                "Selection Preview",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFEBC0),
            )
            Text("Good: $selectedGoodSummary", color = Color(0xFFF4DFC1))
            Text("Evil: $selectedEvilSummary", color = Color(0xFFF4DFC1))
            Text("Modules: $modulesSummary", color = Color(0xFFF4DFC1))
        }
    }
}

private fun pauseTypeLabel(type: NarrationPauseType): String = when (type) {
    NarrationPauseType.ACTION -> "Action Pause"
    NarrationPauseType.STANDARD -> "Standard Pause"
}
