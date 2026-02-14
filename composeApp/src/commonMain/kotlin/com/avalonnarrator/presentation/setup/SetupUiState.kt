package com.avalonnarrator.presentation.setup

import com.avalonnarrator.domain.roles.Alignment
import com.avalonnarrator.domain.roles.RoleCatalog
import com.avalonnarrator.domain.roles.RoleDefinition
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.domain.setup.GameModule
import com.avalonnarrator.domain.setup.SetupIssue

data class SetupUiState(
    val isInitialized: Boolean = false,
    val config: GameSetupConfig = GameSetupConfig(),
    val goodRoles: List<RoleDefinition> = RoleCatalog.byAlignment(Alignment.GOOD),
    val evilRoles: List<RoleDefinition> = RoleCatalog.byAlignment(Alignment.EVIL),
    val loyalServantCount: Int = config.loyalServantAdjustment,
    val minionCount: Int = config.minionAdjustment,
    val selectedCardsCount: Int = config.playerCount,
    val blockingIssues: List<SetupIssue> = emptyList(),
    val nonBlockingIssues: List<SetupIssue> = emptyList(),
    val canStartNarration: Boolean = true,
    val previewRole: RoleDefinition? = null,
    val previewModule: GameModule? = null,
    val selectedInfoCategory: SetupRoleCategory? = null,
)
