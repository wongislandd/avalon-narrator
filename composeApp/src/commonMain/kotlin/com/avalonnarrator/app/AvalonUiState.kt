package com.avalonnarrator.app

import com.avalonnarrator.domain.audio.VoicePackCatalog
import com.avalonnarrator.domain.audio.VoicePackDefinition
import com.avalonnarrator.domain.narration.NarrationPlan
import com.avalonnarrator.domain.roles.Alignment
import com.avalonnarrator.domain.roles.RoleCatalog
import com.avalonnarrator.domain.roles.RoleDefinition
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.domain.setup.SetupIssue
import com.avalonnarrator.navigation.AppScreen

data class AvalonUiState(
    val screen: AppScreen = AppScreen.SETUP,
    val config: GameSetupConfig = GameSetupConfig(),
    val issues: List<SetupIssue> = emptyList(),
    val narrationPlan: NarrationPlan? = null,
    val goodRoles: List<RoleDefinition> = RoleCatalog.byAlignment(Alignment.GOOD),
    val evilRoles: List<RoleDefinition> = RoleCatalog.byAlignment(Alignment.EVIL),
    val availableVoicePacks: List<VoicePackDefinition> = VoicePackCatalog.all(),
    val isInitialized: Boolean = false,
)
