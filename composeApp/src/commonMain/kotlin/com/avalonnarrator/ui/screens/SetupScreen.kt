package com.avalonnarrator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.GameModule
import com.avalonnarrator.presentation.setup.SetupUiEvent
import com.avalonnarrator.presentation.setup.SetupUiState
import com.avalonnarrator.ui.components.HolographicRolePreviewCard
import com.avalonnarrator.ui.components.RoleCard
import kotlinx.coroutines.withTimeoutOrNull
import androidx.compose.foundation.gestures.detectTapGestures

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(
    uiState: SetupUiState,
    onEvent: (SetupUiEvent) -> Unit,
) {
    val contentSidePadding = 16.dp
    val selectedGoodCount = uiState.goodRoles.sumOf { role ->
        if (role.id == RoleId.LOYAL_SERVANT) {
            uiState.loyalServantCount
        } else {
            if (role.id in uiState.config.selectedRoles) 1 else 0
        }
    }
    val selectedEvilCount = uiState.evilRoles.sumOf { role ->
        if (role.id == RoleId.MINION) {
            uiState.minionCount
        } else {
            if (role.id in uiState.config.selectedRoles) 1 else 0
        }
    }
    val selectedModuleCount = listOf(GameModule.EXCALIBUR, GameModule.LADY_OF_LAKE)
        .count { module -> module in uiState.config.enabledModules }

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
                    IconButton(onClick = { onEvent(SetupUiEvent.OpenSettings) }) {
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
                        text = uiState.selectedCardsCount.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFFFF3D6),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = { onEvent(SetupUiEvent.StartNarration) },
                enabled = uiState.canStartNarration,
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
                Text(if (uiState.canStartNarration) "Start Narration" else "Fix Errors To Start")
            }
            Spacer(Modifier.height(14.dp))
            if (uiState.blockingIssues.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF6F1F1A).copy(alpha = 0.92f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = contentSidePadding),
                ) {
                    Column(Modifier.padding(10.dp)) {
                        uiState.blockingIssues.forEach { issue ->
                            Text(text = "• ${issue.message}", color = Color(0xFFFFD9D3))
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
            if (uiState.nonBlockingIssues.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0x665A3C1C),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = contentSidePadding),
                ) {
                    Column(Modifier.padding(10.dp)) {
                        uiState.nonBlockingIssues.forEach { issue ->
                            Text(text = "• ${issue.message}", color = Color(0xFFF3DFBF))
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }

            Text(
                text = "Loyal Servants of Arthur ($selectedGoodCount)",
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
                    val quantity = if (isLoyalServant) uiState.loyalServantCount else null
                    RoleCard(
                        role = role,
                        selected = if (isLoyalServant) (quantity ?: 0) > 0 else role.id in uiState.config.selectedRoles,
                        onToggle = {
                            if (isLoyalServant) {
                                onEvent(SetupUiEvent.ToggleBaseRole(RoleId.LOYAL_SERVANT))
                            } else {
                                onEvent(SetupUiEvent.ToggleRole(role.id))
                            }
                        },
                        onPreviewStart = { onEvent(SetupUiEvent.ShowRolePreview(role)) },
                        onPreviewEnd = { onEvent(SetupUiEvent.HideRolePreview) },
                        showQuantityControls = isLoyalServant && (quantity ?: 0) > 0,
                        quantity = quantity,
                        onDecreaseQuantity = if (isLoyalServant) {
                            {
                                onEvent(SetupUiEvent.DecreaseBaseRole(RoleId.LOYAL_SERVANT))
                            }
                        } else {
                            null
                        },
                        onIncreaseQuantity = if (isLoyalServant) {
                            { onEvent(SetupUiEvent.IncreaseBaseRole(RoleId.LOYAL_SERVANT)) }
                        } else {
                            null
                        },
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            Text(
                text = "Minions of Mordred ($selectedEvilCount)",
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
                    val quantity = if (isMinion) uiState.minionCount else null
                    RoleCard(
                        role = role,
                        selected = if (isMinion) (quantity ?: 0) > 0 else role.id in uiState.config.selectedRoles,
                        onToggle = {
                            if (isMinion) {
                                onEvent(SetupUiEvent.ToggleBaseRole(RoleId.MINION))
                            } else {
                                onEvent(SetupUiEvent.ToggleRole(role.id))
                            }
                        },
                        onPreviewStart = { onEvent(SetupUiEvent.ShowRolePreview(role)) },
                        onPreviewEnd = { onEvent(SetupUiEvent.HideRolePreview) },
                        showQuantityControls = isMinion && (quantity ?: 0) > 0,
                        quantity = quantity,
                        onDecreaseQuantity = if (isMinion) {
                            {
                                onEvent(SetupUiEvent.DecreaseBaseRole(RoleId.MINION))
                            }
                        } else {
                            null
                        },
                        onIncreaseQuantity = if (isMinion) {
                            { onEvent(SetupUiEvent.IncreaseBaseRole(RoleId.MINION)) }
                        } else {
                            null
                        },
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            Text(
                text = "Modules ($selectedModuleCount)",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFFFE4A9),
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
                ModuleSelectionCard(
                    module = GameModule.EXCALIBUR,
                    label = "Excalibur",
                    selected = GameModule.EXCALIBUR in uiState.config.enabledModules,
                    onToggle = { onEvent(SetupUiEvent.ToggleModule(GameModule.EXCALIBUR)) },
                    onPreviewStart = { onEvent(SetupUiEvent.ShowModulePreview(GameModule.EXCALIBUR)) },
                    onPreviewEnd = { onEvent(SetupUiEvent.HideModulePreview) },
                )
                ModuleSelectionCard(
                    module = GameModule.LADY_OF_LAKE,
                    label = "Lady of the Lake",
                    selected = GameModule.LADY_OF_LAKE in uiState.config.enabledModules,
                    onToggle = { onEvent(SetupUiEvent.ToggleModule(GameModule.LADY_OF_LAKE)) },
                    onPreviewStart = { onEvent(SetupUiEvent.ShowModulePreview(GameModule.LADY_OF_LAKE)) },
                    onPreviewEnd = { onEvent(SetupUiEvent.HideModulePreview) },
                )
            }
            Spacer(Modifier.height(24.dp))
        }

        when {
            uiState.previewRole != null -> {
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
                            role = uiState.previewRole,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }

            uiState.previewModule != null -> {
                ModulePreviewOverlay(module = uiState.previewModule)
            }
        }
    }
}

@Composable
private fun ModuleSelectionCard(
    module: GameModule,
    label: String,
    selected: Boolean,
    onToggle: () -> Unit,
    onPreviewStart: () -> Unit,
    onPreviewEnd: () -> Unit,
) {
    val borderColor = if (selected) Color(0xFFFFE8AA) else Color(0xFF6D5631)
    val backgroundBrush = if (selected) {
        Brush.verticalGradient(listOf(Color(0xFF8A6430), Color(0xFF2A1B10)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFF4A3A22), Color(0xFF24170E)))
    }

    Box(
        modifier = Modifier
            .pointerInput(module) {
                detectTapGestures(
                    onTap = { onToggle() },
                    onPress = {
                        val releasedBeforeLongPress = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis.toLong()) {
                            tryAwaitRelease()
                        } ?: false
                        if (!releasedBeforeLongPress) {
                            onPreviewStart()
                            tryAwaitRelease()
                            onPreviewEnd()
                        }
                    },
                )
            }
            .size(width = 148.dp, height = 168.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundBrush)
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(18.dp),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Module",
                color = Color(0xFFDCC394),
                style = MaterialTheme.typography.labelMedium,
            )
        }

        Text(
            text = label,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 10.dp, vertical = 14.dp),
            color = Color(0xFFFFF3D6),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun ModulePreviewOverlay(module: GameModule) {
    val (title, description, gameplayImpact) = modulePreviewContent(module)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xB815100A)),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xE0261A10),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(12.dp),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = title,
                    color = Color(0xFFFFEBC0),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = description,
                    color = Color(0xFFF4E2C2),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Game impact",
                    color = Color(0xFFE7C678),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = gameplayImpact,
                    color = Color(0xFFFFF3D6),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

private fun modulePreviewContent(module: GameModule): Triple<String, String, String> = when (module) {
    GameModule.EXCALIBUR -> Triple(
        "Excalibur",
        "Adds the Excalibur token to the game and assigns it to a player on the quest team.",
        "Before quest cards are revealed, the Excalibur holder may force one quest-card switch between questing players, changing how that quest resolves.",
    )

    GameModule.LADY_OF_LAKE -> Triple(
        "Lady of the Lake",
        "Adds the Lady of the Lake token and loyalty-check action.",
        "During Lady of the Lake moments, the holder inspects one player's alignment, then passes the token to that inspected player, creating new information and social pressure.",
    )

    GameModule.LANCELOT -> Triple(
        "Lancelot",
        "Adds the Good and Evil Lancelot roles.",
        "This module is inferred from selected roles and can introduce allegiance uncertainty in Lancelot variants.",
    )
}
