package com.avalonnarrator.engine.rules

import com.avalonnarrator.domain.audio.ClipId
import com.avalonnarrator.domain.setup.GameModule
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.domain.roles.RoleId

enum class RulePauseType {
    REGULAR,
    ACTION,
}

data class RuleCondition(
    val requiresAllRoles: Set<RoleId> = emptySet(),
    val requiresAnyRoles: Set<RoleId> = emptySet(),
    val excludesRoles: Set<RoleId> = emptySet(),
    val requiredModules: Set<GameModule> = emptySet(),
    val requiresNarrationRemindersEnabled: Boolean? = null,
    val minPlayers: Int? = null,
    val maxPlayers: Int? = null,
) {
    fun matches(config: GameSetupConfig, activeRoles: Set<RoleId> = config.selectedRoles): Boolean {
        val effectiveModules = buildSet {
            addAll(config.enabledModules - GameModule.LANCELOT)
            if (RoleId.LANCELOT_GOOD in activeRoles || RoleId.LANCELOT_EVIL in activeRoles) {
                add(GameModule.LANCELOT)
            }
        }
        if (!activeRoles.containsAll(requiresAllRoles)) return false
        if (requiresAnyRoles.isNotEmpty() && requiresAnyRoles.none(activeRoles::contains)) return false
        if (activeRoles.any(excludesRoles::contains)) return false
        if (!effectiveModules.containsAll(requiredModules)) return false
        if (requiresNarrationRemindersEnabled != null && config.narrationRemindersEnabled != requiresNarrationRemindersEnabled) return false
        if (minPlayers != null && config.playerCount < minPlayers) return false
        if (maxPlayers != null && config.playerCount > maxPlayers) return false
        return true
    }
}

data class RuleStepDefinition(
    val id: String,
    val phase: RulePhase,
    val order: Int,
    val condition: RuleCondition,
    val clips: List<ClipId>,
    val interClipDelayMs: Long,
    val baseDelayMs: Long,
    val interClipPauseType: RulePauseType,
    val postStepPauseType: RulePauseType,
)
