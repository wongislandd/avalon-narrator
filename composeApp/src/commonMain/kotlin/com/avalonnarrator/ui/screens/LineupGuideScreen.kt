package com.avalonnarrator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avalonnarrator.domain.recommendation.LineupFlavor
import com.avalonnarrator.domain.recommendation.RecommendedLineupDefinition
import com.avalonnarrator.presentation.lineups.LineupGuideUiEvent
import com.avalonnarrator.presentation.lineups.LineupGuideUiState

@Composable
fun LineupGuideScreen(
    uiState: LineupGuideUiState,
    onEvent: (LineupGuideUiEvent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1C1208), Color(0xFF422C16), Color(0xFF73512B)),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .navigationBarsPadding(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { onEvent(LineupGuideUiEvent.Back) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFFFEBC0),
                    )
                }
                Column {
                    Text(
                        text = "Lineup Codex",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFFFEBC0),
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Recommended setups for each table size.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE6D3B2),
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            PlayerCountSelector(
                playerCount = uiState.selectedPlayerCount,
                onDecrease = { onEvent(LineupGuideUiEvent.DecreasePlayers) },
                onIncrease = { onEvent(LineupGuideUiEvent.IncreasePlayers) },
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.lineups, key = { lineup -> lineup.id }) { lineup ->
                    RecommendedLineupCard(
                        lineup = lineup,
                        onApply = { onEvent(LineupGuideUiEvent.ApplyLineup(lineup)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerCountSelector(
    playerCount: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x6629170D),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0x66D5AA62), RoundedCornerShape(16.dp)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            CountButton(label = "-", onClick = onDecrease)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$playerCount",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color(0xFFFFE9B6),
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = if (playerCount == 1) "Player" else "Players",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFFFE4A9),
                    fontWeight = FontWeight.SemiBold,
                )
            }
            CountButton(label = "+", onClick = onIncrease)
        }
    }
}

@Composable
private fun CountButton(
    label: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(44.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF9C7A35),
            contentColor = Color(0xFFFFF3D6),
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
    ) {
        Text(text = label, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RecommendedLineupCard(
    lineup: RecommendedLineupDefinition,
    onApply: () -> Unit,
) {
    val gauge = gaugePresentation(lineup.flavor)
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
                text = lineup.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFFFEBC0),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = lineup.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE8D5B6),
            )

            Text(
                text = "Good: ${lineup.goodRolesSummary()}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFD6EBFF),
            )
            Text(
                text = "Evil: ${lineup.evilRolesSummary()}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFFFD3CC),
            )
            Text(
                text = "Modules: ${lineup.modulesSummary()}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFFFEBC0),
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Alignment Lean: ${gauge.label}",
                    style = MaterialTheme.typography.labelLarge,
                    color = gauge.accent,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Spectrum",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFE6D3B2),
                )
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF5B9BD6), Color(0xFFCFB06D), Color(0xFFC05A4D)),
                            ),
                            shape = RoundedCornerShape(8.dp),
                        ),
                ) {
                    val markerStart: Dp = ((maxWidth - 2.dp) * gauge.position)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(2.dp)
                            .align(Alignment.CenterStart)
                            .offset(x = markerStart)
                            .background(gauge.accent),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Favors Good",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFA6D6FF),
                    )
                    Text(
                        text = "Balanced",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFE2A6),
                    )
                    Text(
                        text = "Favors Evil",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFB6AB),
                    )
                }
            }

            Button(
                onClick = onApply,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9C7A35),
                    contentColor = Color(0xFFFFF3D6),
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Apply")
            }
        }
    }
}

private data class GaugePresentation(
    val label: String,
    val accent: Color,
    val position: Float,
)

private fun gaugePresentation(flavor: LineupFlavor): GaugePresentation = when (flavor) {
    LineupFlavor.FAVORS_GOOD -> GaugePresentation(
        label = "Favors Good",
        accent = Color(0xFFA6D6FF),
        position = 0.14f,
    )

    LineupFlavor.BALANCED -> GaugePresentation(
        label = "Balanced",
        accent = Color(0xFFFFE2A6),
        position = 0.5f,
    )

    LineupFlavor.FAVORS_EVIL -> GaugePresentation(
        label = "Favors Evil",
        accent = Color(0xFFFFB6AB),
        position = 0.86f,
    )

    LineupFlavor.CHAOTIC -> GaugePresentation(
        label = "Balanced",
        accent = Color(0xFFF5C490),
        position = 0.5f,
    )
}
