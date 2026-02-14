package com.avalonnarrator.domain.usecase.setup

import com.avalonnarrator.domain.audio.VoicePackId
import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.GameModule
import com.avalonnarrator.domain.setup.NarrationPace

sealed interface SetupMutation {
    data class ToggleRole(val roleId: RoleId) : SetupMutation
    data class SetLoyalServantCount(val count: Int) : SetupMutation
    data class SetMinionCount(val count: Int) : SetupMutation
    data class ToggleModule(val module: GameModule) : SetupMutation
    data class SetNarrationPace(val pace: NarrationPace) : SetupMutation
    data object RegenerateSeed : SetupMutation
    data class SetVoicePack(val voicePackId: VoicePackId) : SetupMutation
    data class ApplyRecommendedLineup(
        val specialRoles: Set<RoleId>,
        val loyalServantCount: Int,
        val minionCount: Int,
        val enabledModules: Set<GameModule> = emptySet(),
    ) : SetupMutation
    data class SetDebugTimelineEnabled(val enabled: Boolean) : SetupMutation
    data class SetValidatorsEnabled(val enabled: Boolean) : SetupMutation
    data class SetNarrationRemindersEnabled(val enabled: Boolean) : SetupMutation
}
