package com.avalonnarrator.domain.usecase.setup

import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.GameModule
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.domain.setup.RosterBuilder

class MutateSetupUseCase(
    private val derivePlayerCount: DerivePlayerCountUseCase = DerivePlayerCountUseCase(),
) {
    fun normalize(config: GameSetupConfig): GameSetupConfig {
        val selectedSpecial = config.selectedRoles
            .filter { it in RosterBuilder.selectableRoleIds() }
            .toSet()
        val loyalServantCount = config.loyalServantAdjustment.coerceIn(0, 12)
        val minionCount = config.minionAdjustment.coerceIn(0, 12)
        val derivedPlayerCount = derivePlayerCount(
            selectedSpecialRoles = selectedSpecial,
            loyalServantCount = loyalServantCount,
            minionCount = minionCount,
        )
        val inferredModules = buildSet {
            addAll(config.enabledModules - GameModule.LANCELOT)
            if (RoleId.LANCELOT_GOOD in selectedSpecial || RoleId.LANCELOT_EVIL in selectedSpecial) {
                add(GameModule.LANCELOT)
            }
        }
        return config.copy(
            playerCount = derivedPlayerCount,
            selectedRoles = selectedSpecial,
            loyalServantAdjustment = loyalServantCount,
            minionAdjustment = minionCount,
            enabledModules = inferredModules,
        )
    }

    operator fun invoke(current: GameSetupConfig, mutation: SetupMutation): GameSetupConfig {
        val next = when (mutation) {
            is SetupMutation.ToggleRole -> {
                if (mutation.roleId == RoleId.LOYAL_SERVANT || mutation.roleId == RoleId.MINION) {
                    current
                } else {
                    val nextRoles = current.selectedRoles.toMutableSet()
                    if (mutation.roleId in nextRoles) {
                        nextRoles.remove(mutation.roleId)
                    } else {
                        nextRoles.add(mutation.roleId)
                    }
                    current.copy(selectedRoles = nextRoles)
                }
            }

            is SetupMutation.SetLoyalServantCount -> {
                current.copy(loyalServantAdjustment = mutation.count.coerceIn(0, 12))
            }

            is SetupMutation.SetMinionCount -> {
                current.copy(minionAdjustment = mutation.count.coerceIn(0, 12))
            }

            is SetupMutation.ToggleModule -> {
                if (mutation.module == GameModule.LANCELOT) {
                    current
                } else {
                    val modules = current.enabledModules.toMutableSet().apply {
                        if (mutation.module in this) remove(mutation.module) else add(mutation.module)
                    }
                    current.copy(enabledModules = modules)
                }
            }

            is SetupMutation.SetRegularPauseMs -> current.copy(regularPauseMs = mutation.value.coerceIn(0, 15_000))
            is SetupMutation.SetActionPauseMs -> current.copy(actionPauseMs = mutation.value.coerceIn(0, 15_000))
            is SetupMutation.SetVoicePack -> current.copy(selectedVoicePack = mutation.voicePackId)
            is SetupMutation.ApplyRecommendedLineup -> {
                current.copy(
                    selectedRoles = mutation.specialRoles,
                    loyalServantAdjustment = mutation.loyalServantCount,
                    minionAdjustment = mutation.minionCount,
                    enabledModules = mutation.enabledModules,
                )
            }
            is SetupMutation.SetValidatorsEnabled -> current.copy(validatorsEnabled = mutation.enabled)
            is SetupMutation.SetNarrationRemindersEnabled -> current.copy(narrationRemindersEnabled = mutation.enabled)
        }
        return normalize(next)
    }
}
