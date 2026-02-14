package com.avalonnarrator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avalonnarrator.app.AvalonUiState
import com.avalonnarrator.domain.roles.RoleDefinition
import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.RosterBuilder
import com.avalonnarrator.domain.setup.SetupIssueLevel
import com.avalonnarrator.ui.components.HolographicRolePreviewCard
import com.avalonnarrator.ui.components.RoleCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(
    uiState: AvalonUiState,
    onToggleRole: (RoleId) -> Unit,
    onIncreaseLoyalServants: () -> Unit,
    onDecreaseLoyalServants: () -> Unit,
    onIncreaseMinions: () -> Unit,
    onDecreaseMinions: () -> Unit,
    onToggleLoyalServantSelection: () -> Unit,
    onToggleMinionSelection: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartRun: () -> Unit,
) {
    val contentSidePadding = 16.dp
    var previewRole by remember { mutableStateOf<RoleDefinition?>(null) }
    val roster = RosterBuilder.build(uiState.config)
    val selectedCardsCount = uiState.config.selectedRoles.size + roster.loyalServantCount + roster.minionCount
    val blockingIssueCodes = setOf("MIN_PLAYERS_NOT_MET", "MAX_PLAYERS_EXCEEDED", "LANCELOT_PAIR_REQUIRED")
    val blockingIssues = uiState.issues.filter { it.level == SetupIssueLevel.ERROR || it.code in blockingIssueCodes }
    val nonBlockingIssues = uiState.issues.filterNot { it.level == SetupIssueLevel.ERROR || it.code in blockingIssueCodes }
    val canStartNarration = blockingIssues.isEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF22170D), Color(0xFF4B3520), Color(0xFF7C5D34)),
                ),
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = contentSidePadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Avalon Narrator",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFFFFEBC0),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0x5C291A0F),
                    modifier = Modifier.size(44.dp),
                ) {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFFFFEBC0),
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = contentSidePadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Characters Selected",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFFFE4A9),
                    modifier = Modifier.weight(1f),
                )
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0x5C291A0F),
                ) {
                    Text(
                        text = selectedCardsCount.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFFFF3D6),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onStartRun,
                enabled = canStartNarration,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9C7A35),
                    contentColor = Color(0xFFFFF3D6),
                    disabledContainerColor = Color(0x704A3922),
                    disabledContentColor = Color(0xFFD7C7A8),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = contentSidePadding),
            ) {
                Text(if (canStartNarration) "Start Narration" else "Fix Errors To Start")
            }
            Spacer(Modifier.height(14.dp))
            if (blockingIssues.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF6F1F1A).copy(alpha = 0.92f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = contentSidePadding),
                ) {
                    Column(Modifier.padding(10.dp)) {
                        blockingIssues.forEach { issue ->
                            Text(text = "• ${issue.message}", color = Color(0xFFFFD9D3))
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
            if (nonBlockingIssues.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0x665A3C1C),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = contentSidePadding),
                ) {
                    Column(Modifier.padding(10.dp)) {
                        nonBlockingIssues.forEach { issue ->
                            Text(text = "• ${issue.message}", color = Color(0xFFF3DFBF))
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }

            Text(
                text = "Loyal Servants of Arthur",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFE0F0FF),
                modifier = Modifier.padding(horizontal = contentSidePadding),
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                maxItemsInEachRow = 2,
            ) {
                uiState.goodRoles.forEach { role ->
                    val isLoyalServant = role.id == RoleId.LOYAL_SERVANT
                    val quantity = if (isLoyalServant) roster.loyalServantCount else null
                    RoleCard(
                        role = role,
                        selected = if (isLoyalServant) (quantity ?: 0) > 0 else role.id in uiState.config.selectedRoles,
                        onToggle = {
                            if (isLoyalServant) {
                                onToggleLoyalServantSelection()
                            } else {
                                onToggleRole(role.id)
                            }
                        },
                        onPreviewStart = { previewRole = role },
                        onPreviewEnd = { previewRole = null },
                        showQuantityControls = isLoyalServant && (quantity ?: 0) > 0,
                        quantity = quantity,
                        onDecreaseQuantity = if (isLoyalServant) {
                            {
                                onDecreaseLoyalServants()
                            }
                        } else {
                            null
                        },
                        onIncreaseQuantity = if (isLoyalServant) onIncreaseLoyalServants else null,
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            Text(
                text = "Minions of Mordred",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFFFD4CD),
                modifier = Modifier.padding(horizontal = contentSidePadding),
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                maxItemsInEachRow = 2,
            ) {
                uiState.evilRoles.forEach { role ->
                    val isMinion = role.id == RoleId.MINION
                    val quantity = if (isMinion) roster.minionCount else null
                    RoleCard(
                        role = role,
                        selected = if (isMinion) (quantity ?: 0) > 0 else role.id in uiState.config.selectedRoles,
                        onToggle = {
                            if (isMinion) {
                                onToggleMinionSelection()
                            } else {
                                onToggleRole(role.id)
                            }
                        },
                        onPreviewStart = { previewRole = role },
                        onPreviewEnd = { previewRole = null },
                        showQuantityControls = isMinion && (quantity ?: 0) > 0,
                        quantity = quantity,
                        onDecreaseQuantity = if (isMinion) {
                            {
                                onDecreaseMinions()
                            }
                        } else {
                            null
                        },
                        onIncreaseQuantity = if (isMinion) onIncreaseMinions else null,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        previewRole?.let { role ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xB815100A)),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.78f)
                        .aspectRatio(0.71f)
                        .padding(8.dp),
                ) {
                    HolographicRolePreviewCard(
                        role = role,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}
