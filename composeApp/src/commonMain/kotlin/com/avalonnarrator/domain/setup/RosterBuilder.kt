package com.avalonnarrator.domain.setup

import com.avalonnarrator.domain.roles.RoleId

object RosterBuilder {
    private val autoFillRoles = setOf(RoleId.LOYAL_SERVANT, RoleId.MINION)

    data class Composition(
        val playerCount: Int,
        val goodSlots: Int,
        val evilSlots: Int,
        val selectedSpecialRoles: Set<RoleId>,
        val autoLoyalServantCount: Int,
        val autoMinionCount: Int,
        val loyalServantCount: Int,
        val minionCount: Int,
        val effectiveRoles: Set<RoleId>,
    )

    fun selectableRoleIds(): Set<RoleId> = RoleId.entries.toSet() - autoFillRoles

    fun build(config: GameSetupConfig): Composition {
        val selectedSpecialRoles = config.selectedRoles.filter { it in selectableRoleIds() }.toSet()
        val evilSlots = expectedEvilCount(config.playerCount) ?: 0
        val goodSlots = (config.playerCount - evilSlots).coerceAtLeast(0)

        // Base-role quantities are user-controlled and independent from special role picks.
        val autoLoyalServantCount = 0
        val autoMinionCount = 0

        val loyalServantCount = config.loyalServantAdjustment.coerceIn(0, 12)
        val minionCount = config.minionAdjustment.coerceIn(0, 12)

        val effectiveRoles = buildSet {
            addAll(selectedSpecialRoles)
            if (loyalServantCount > 0) add(RoleId.LOYAL_SERVANT)
            if (minionCount > 0) add(RoleId.MINION)
        }

        return Composition(
            playerCount = config.playerCount,
            goodSlots = goodSlots,
            evilSlots = evilSlots,
            selectedSpecialRoles = selectedSpecialRoles,
            autoLoyalServantCount = autoLoyalServantCount,
            autoMinionCount = autoMinionCount,
            loyalServantCount = loyalServantCount,
            minionCount = minionCount,
            effectiveRoles = effectiveRoles,
        )
    }

    fun expectedEvilCount(playerCount: Int): Int? = when (playerCount) {
        5, 6 -> 2
        7, 8, 9 -> 3
        10 -> 4
        else -> null
    }
}
