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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.avalonnarrator.app.AvalonUiState
import com.avalonnarrator.domain.setup.GameModule
import com.avalonnarrator.domain.setup.NarrationPace

@Composable
fun SettingsScreen(
    uiState: AvalonUiState,
    onBack: () -> Unit,
    onToggleModule: (GameModule) -> Unit,
    onValidatorsChanged: (Boolean) -> Unit,
    onPaceChanged: (NarrationPace) -> Unit,
    onRegenerateSeed: () -> Unit,
    onVoicePackChanged: (com.avalonnarrator.domain.audio.VoicePackId) -> Unit,
    onNarrationRemindersChanged: (Boolean) -> Unit,
    onDebugTimelineChanged: (Boolean) -> Unit,
) {
    val config = uiState.config

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
            Text(
                "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFEBC0),
            )
            Spacer(Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0x5C291A0F),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Player Count (Derived)",
                        color = Color(0xFFFFE4A9),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = config.playerCount.toString(),
                        color = Color(0xFFFFF3D6),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            ModuleRow(
                label = "Enable Setup Validators",
                checked = config.validatorsEnabled,
                onCheckedChange = onValidatorsChanged,
            )

            Spacer(Modifier.height(16.dp))
            SectionTitle("Modules")
            Spacer(Modifier.height(6.dp))
            ModuleRow(
                label = "Excalibur",
                checked = GameModule.EXCALIBUR in config.enabledModules,
                onCheckedChange = { onToggleModule(GameModule.EXCALIBUR) },
            )
            ModuleRow(
                label = "Lady of the Lake",
                checked = GameModule.LADY_OF_LAKE in config.enabledModules,
                onCheckedChange = { onToggleModule(GameModule.LADY_OF_LAKE) },
            )
            Text(
                text = "Lancelot module is inferred when Lancelot roles are selected.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFE6D3B2),
                modifier = Modifier.padding(top = 6.dp, start = 4.dp, end = 4.dp),
            )

            Spacer(Modifier.height(16.dp))
            SectionTitle("Narration Pace")
            Spacer(Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0x5C291A0F),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x66D5AA62), RoundedCornerShape(14.dp)),
            ) {
                Column(Modifier.padding(vertical = 6.dp)) {
                    NarrationPace.entries.forEach { pace ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = pace == config.narrationPace,
                                onClick = { onPaceChanged(pace) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFFE6C374),
                                    unselectedColor = Color(0xFFB79A67),
                                ),
                            )
                            Text(
                                pace.name.lowercase().replaceFirstChar(Char::titlecase),
                                color = Color(0xFFFFEBC0),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            SectionTitle("Voice Pack")
            Spacer(Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0x5C291A0F),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x66D5AA62), RoundedCornerShape(14.dp)),
            ) {
                Column(Modifier.padding(vertical = 6.dp)) {
                    uiState.availableVoicePacks.forEach { voicePack ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = voicePack.id == config.selectedVoicePack,
                                onClick = { onVoicePackChanged(voicePack.id) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFFE6C374),
                                    unselectedColor = Color(0xFFB79A67),
                                ),
                            )
                            Text(voicePack.displayName, color = Color(0xFFFFEBC0))
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0x5C291A0F),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        "Random Seed: ${config.randomSeed ?: "Not set"}",
                        color = Color(0xFFFFEBC0),
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = onRegenerateSeed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9C7A35),
                            contentColor = Color(0xFFFFF3D6),
                        ),
                    ) {
                        Text("Regenerate Seed")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            ModuleRow(
                label = "Include Role Reminders",
                checked = config.narrationRemindersEnabled,
                onCheckedChange = onNarrationRemindersChanged,
            )

            Spacer(Modifier.height(10.dp))
            ModuleRow(
                label = "Show Debug Timeline",
                checked = config.debugTimelineEnabled,
                onCheckedChange = onDebugTimelineChanged,
            )

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9C7A35),
                    contentColor = Color(0xFFFFF3D6),
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Back")
            }
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
            Text(label, color = Color(0xFFFFEBC0))
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
