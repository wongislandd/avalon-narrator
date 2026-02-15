package com.avalonnarrator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avalonnarrator.domain.roles.RoleDefinition
import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.presentation.setup.SetupRoleCategory
import com.avalonnarrator.presentation.setup.SetupUiEvent
import com.avalonnarrator.presentation.setup.SetupUiState
import com.avalonnarrator.ui.components.CharacterArtwork

@Composable
fun SetupRoleInfoScreen(
    uiState: SetupUiState,
    onEvent: (SetupUiEvent) -> Unit,
) {
    val category = uiState.selectedInfoCategory
    val roles = when (category) {
        SetupRoleCategory.GOOD -> uiState.goodRoles
        SetupRoleCategory.EVIL -> uiState.evilRoles
        null -> emptyList()
    }
    val title = when (category) {
        SetupRoleCategory.GOOD -> "Loyal Servants of Arthur"
        SetupRoleCategory.EVIL -> "Minions of Mordred"
        null -> "Role Info"
    }
    val accent = when (category) {
        SetupRoleCategory.GOOD -> Color(0xFFE0F0FF)
        SetupRoleCategory.EVIL -> Color(0xFFFFD4CD)
        null -> Color(0xFFFFEBC0)
    }

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
                .padding(vertical = 16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { onEvent(SetupUiEvent.CloseRoleCategoryInfo) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFFFEBC0),
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = accent,
                    fontWeight = FontWeight.Bold,
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            ) {
                items(roles, key = { it.id.name }) { role ->
                    val selected = isRoleSelected(uiState = uiState, roleId = role.id)
                    RoleInfoRow(
                        role = role,
                        selected = selected,
                        onClick = {
                            when (role.id) {
                                RoleId.LOYAL_SERVANT,
                                RoleId.MINION -> onEvent(SetupUiEvent.ToggleBaseRole(role.id))

                                else -> onEvent(SetupUiEvent.ToggleRole(role.id))
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleInfoRow(
    role: RoleDefinition,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) Color(0xFFE6C374) else Color(0x668D6A35)
    val rowBackground = if (selected) Color(0x80325366) else Color(0x6620140B)

    Surface(
        color = rowBackground,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier.size(width = 52.dp, height = 62.dp),
                contentAlignment = Alignment.Center,
            ) {
                CharacterArtwork(
                    imageKey = role.imageKey,
                    contentDescription = role.name,
                    modifier = Modifier
                        .size(width = 48.dp, height = 56.dp)
                        .clip(RoundedCornerShape(10.dp)),
                )
            }
            Text(
                text = buildAnnotatedString {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(role.name)
                    pop()
                    append(": ")
                    append(role.mechanicSummary)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFFFF3D6),
            )
        }
    }
}

private fun isRoleSelected(
    uiState: SetupUiState,
    roleId: RoleId,
): Boolean = when (roleId) {
    RoleId.LOYAL_SERVANT -> uiState.loyalServantCount > 0
    RoleId.MINION -> uiState.minionCount > 0
    else -> roleId in uiState.config.selectedRoles
}
