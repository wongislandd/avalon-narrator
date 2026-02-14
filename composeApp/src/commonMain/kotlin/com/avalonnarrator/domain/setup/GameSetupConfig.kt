package com.avalonnarrator.domain.setup

import com.avalonnarrator.domain.audio.VoicePackId
import com.avalonnarrator.domain.roles.RoleId

data class GameSetupConfig(
    val playerCount: Int = 5,
    val selectedRoles: Set<RoleId> = setOf(RoleId.MERLIN, RoleId.ASSASSIN),
    val loyalServantAdjustment: Int = 2,
    val minionAdjustment: Int = 1,
    val validatorsEnabled: Boolean = true,
    val enabledModules: Set<GameModule> = emptySet(),
    val narrationPace: NarrationPace = NarrationPace.NORMAL,
    val randomSeed: Long? = null,
    val selectedVoicePack: VoicePackId = VoicePackId.DRAMATIC_EN,
    val narrationRemindersEnabled: Boolean = false,
    val debugTimelineEnabled: Boolean = false,
)
