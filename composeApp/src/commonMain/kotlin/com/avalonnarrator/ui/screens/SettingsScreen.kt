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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
fun SettingsScreen(
    uiState: SettingsUiState,
    onEvent: (SettingsUiEvent) -> Unit,
) {
    val config = uiState.config
    val selectedVoicePack = uiState.availableVoicePacks.firstOrNull { it.id == config.selectedVoicePack }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF22170D), Color(0xFF4B3520), Color(0xFF7C5D34)),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { onEvent(SettingsUiEvent.Back) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFFFEBC0),
                    )
                }
                Text(
                    "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFEBC0),
                )
            }

            Spacer(Modifier.height(16.dp))
            SectionTitle("Voice Pack")
            Spacer(Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0x5C291A0F),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x66D5AA62), RoundedCornerShape(14.dp)),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Selected: ${selectedVoicePack?.displayName ?: config.selectedVoicePack}",
                        color = Color(0xFFFFEBC0),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (!selectedVoicePack?.description.isNullOrBlank()) {
                        Text(
                            selectedVoicePack?.description.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE6D3B2),
                        )
                    }
                    Button(
                        onClick = { onEvent(SettingsUiEvent.OpenVoiceSelection) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9C7A35),
                            contentColor = Color(0xFFFFF3D6),
                        ),
                    ) {
                        Text("Open Voice Selection")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            SectionTitle("Pauses")
            Spacer(Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0x5C291A0F),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x66D5AA62), RoundedCornerShape(14.dp)),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    PauseControlRow(
                        label = "Regular Pause",
                        description = "Used for normal waits and quick actions like thumbs up/down and close eyes (default 1s).",
                        valueMs = config.regularPauseMs,
                        onDecrease = { onEvent(SettingsUiEvent.DecreaseRegularPause) },
                        onIncrease = { onEvent(SettingsUiEvent.IncreaseRegularPause) },
                    )
                    PauseControlRow(
                        label = "Action Pause",
                        description = "Used for inspection windows like open eyes/wake calls (default 4s).",
                        valueMs = config.actionPauseMs,
                        onDecrease = { onEvent(SettingsUiEvent.DecreaseActionPause) },
                        onIncrease = { onEvent(SettingsUiEvent.IncreaseActionPause) },
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            ModuleRow(
                label = "Include Role Reminders",
                description = "Enables readout of important information on characters during the narration phase.",
                checked = config.narrationRemindersEnabled,
                onCheckedChange = { onEvent(SettingsUiEvent.ToggleReminders(it)) },
            )

            Spacer(Modifier.height(16.dp))
            ModuleRow(
                label = "Enforce game rules",
                description = "When toggled on, the app will strictly enforce game mechanics. Turn off if playing with custom rules.",
                checked = config.validatorsEnabled,
                onCheckedChange = { onEvent(SettingsUiEvent.ToggleValidators(it)) },
            )

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = Color(0xFFFFE4A9),
    )
}

@Composable
private fun ModuleRow(
    label: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0x5C291A0F),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0x66D5AA62), RoundedCornerShape(14.dp)),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(label, color = Color(0xFFFFEBC0))
                if (!description.isNullOrBlank()) {
                    Text(
                        text = description,
                        color = Color(0xFFE6D3B2),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFFFF3D6),
                    checkedTrackColor = Color(0xFF9C7A35),
                    uncheckedThumbColor = Color(0xFFD4BE95),
                    uncheckedTrackColor = Color(0x66443322),
                ),
            )
        }
    }
}

@Composable
private fun PauseControlRow(
    label: String,
    description: String,
    valueMs: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = label, color = Color(0xFFFFEBC0))
            Text(
                text = description,
                color = Color(0xFFE6D3B2),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = formatPauseSeconds(valueMs),
                color = Color(0xFFE6D3B2),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onDecrease,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x7A3C2A15),
                    contentColor = Color(0xFFFFEBC0),
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text("-")
            }
            Button(
                onClick = onIncrease,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9C7A35),
                    contentColor = Color(0xFFFFF3D6),
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text("+")
            }
        }
    }
}

private fun formatPauseSeconds(valueMs: Int): String =
    if (valueMs % 1000 == 0) {
        "${valueMs / 1000}s"
    } else {
        "${valueMs / 1000.0}s"
    }
