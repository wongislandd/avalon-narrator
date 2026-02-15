package com.avalonnarrator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avalonnarrator.presentation.settings.SettingsUiEvent
import com.avalonnarrator.presentation.settings.SettingsUiState

@Composable
fun VoicePackSelectionScreen(
    uiState: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1208), Color(0xFF3E2A17), Color(0xFF6D4F2C)),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { onEvent(SettingsUiEvent.CloseVoiceSelection) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFFFEBC0),
                    )
                }
                Text(
                    text = "Voice Selection",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFFFFEBC0),
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Use the speaker icon to preview a random clip.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFE6D3B2),
            )

            Spacer(Modifier.height(14.dp))
            uiState.availableVoicePacks.forEach { voicePack ->
                val isSelected = voicePack.id == uiState.config.selectedVoicePack
                val isPreviewing = voicePack.id == uiState.previewingVoicePackId
                val borderColor = when {
                    isPreviewing -> Color(0xFFE6C374)
                    isSelected -> Color(0xFFB7E08C)
                    else -> Color(0x668D6A35)
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0x5523170C),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, borderColor, RoundedCornerShape(14.dp)),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = voicePack.displayName,
                                    color = Color(0xFFFFEBC0),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                if (voicePack.description.isNotBlank()) {
                                    Text(
                                        text = voicePack.description,
                                        color = Color(0xFFE6D3B2),
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            }
                            IconButton(
                                onClick = { onEvent(SettingsUiEvent.PreviewVoicePack(voicePack.id)) },
                                enabled = !isPreviewing,
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Preview ${voicePack.displayName}",
                                    tint = if (isPreviewing) Color(0xFFE6C374) else Color(0xFFFFEBC0),
                                )
                            }
                        }

                        Button(
                            onClick = { onEvent(SettingsUiEvent.SetVoicePack(voicePack.id)) },
                            enabled = !isSelected,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9C7A35),
                                contentColor = Color(0xFFFFF3D6),
                                disabledContainerColor = Color(0x704A3922),
                                disabledContentColor = Color(0xFFD7C7A8),
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(if (isSelected) "Selected" else "Use This Voice")
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

        }
    }
}
