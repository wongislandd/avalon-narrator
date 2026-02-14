package com.avalonnarrator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.avalonnarrator.domain.setup.GameModule
import com.avalonnarrator.presentation.setup.SetupUiEvent
import com.avalonnarrator.presentation.setup.SetupUiState

@Composable
fun SetupModuleInfoScreen(
    uiState: SetupUiState,
    onEvent: (SetupUiEvent) -> Unit,
) {
    val modules = listOf(GameModule.EXCALIBUR, GameModule.LADY_OF_LAKE)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF22170D), Color(0xFF4B3520), Color(0xFF7C5D34)),
                ),
            )
            .statusBarsPadding()
            .padding(vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { onEvent(SetupUiEvent.CloseModuleInfo) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFFFFEBC0),
                )
            }
            Text(
                text = "Module Codex",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFFFFEBC0),
                fontWeight = FontWeight.Bold,
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        ) {
            items(modules, key = { it.name }) { module ->
                val content = moduleGuideContent(module)
                val isEnabled = module in uiState.config.enabledModules

                Surface(
                    color = Color(0x6620140B),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0x668D6A35), RoundedCornerShape(14.dp)),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = content.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFFFEBC0),
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = content.shortLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFFE5C57C),
                        )
                        Text(
                            text = content.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFF3E1C4),
                        )
                        Text(
                            text = "Game impact: ${content.gameplayImpact}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFF3D6),
                        )
                        Text(
                            text = if (isEnabled) "Status: Enabled" else "Status: Disabled",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isEnabled) Color(0xFFD8F4D6) else Color(0xFFFFD3CC),
                            fontWeight = FontWeight.SemiBold,
                        )
                        Button(
                            onClick = { onEvent(SetupUiEvent.ToggleModule(module)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9C7A35),
                                contentColor = Color(0xFFFFF3D6),
                            ),
                        ) {
                            Text(if (isEnabled) "Disable Module" else "Enable Module")
                        }
                    }
                }
            }
        }
    }
}
