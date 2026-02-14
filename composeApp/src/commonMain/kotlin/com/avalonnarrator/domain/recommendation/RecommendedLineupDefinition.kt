package com.avalonnarrator.domain.recommendation

import com.avalonnarrator.domain.roles.Alignment
import com.avalonnarrator.domain.roles.RoleCatalog
import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.RosterBuilder

data class RecommendedLineupDefinition(
    val id: String,
    val title: String,
    val playerCount: Int,
    val description: String,
    val flavor: LineupFlavor,
    val specialRoles: Set<RoleId>,
    val loyalServants: Int,
    val minions: Int,
) {
    init {
        require(playerCount in 5..10) { "Lineup player count must be between 5 and 10." }
        require(loyalServants >= 0) { "Loyal servants cannot be negative." }
        require(minions >= 0) { "Minions cannot be negative." }

        val totalAssigned = specialRoles.size + loyalServants + minions
        require(totalAssigned == playerCount) {
            "Lineup $id assigns $totalAssigned cards but declares $playerCount players."
        }

        val specialEvilCount = specialRoles.count { roleId ->
            RoleCatalog.byId(roleId)?.alignment == Alignment.EVIL
        }
        val evilCount = specialEvilCount + minions
        val expectedEvilCount = RosterBuilder.expectedEvilCount(playerCount)
        require(expectedEvilCount == null || evilCount == expectedEvilCount) {
            "Lineup $id has $evilCount evil roles but expected $expectedEvilCount for $playerCount players."
        }
    }

    fun goodRolesSummary(): String {
        val goodSpecial = specialRoles
            .filter { roleId -> RoleCatalog.byId(roleId)?.alignment == Alignment.GOOD }
            .sortedBy { roleId -> RoleCatalog.byId(roleId)?.name ?: roleId.name }
            .map { roleId -> RoleCatalog.byId(roleId)?.name ?: roleId.name }
            .toMutableList()
        if (loyalServants > 0) {
            goodSpecial += if (loyalServants == 1) "Loyal Servant" else "Loyal Servant x$loyalServants"
        }
        return goodSpecial.joinToString(", ")
    }

    fun evilRolesSummary(): String {
        val evilSpecial = specialRoles
            .filter { roleId -> RoleCatalog.byId(roleId)?.alignment == Alignment.EVIL }
            .sortedBy { roleId -> RoleCatalog.byId(roleId)?.name ?: roleId.name }
            .map { roleId -> RoleCatalog.byId(roleId)?.name ?: roleId.name }
            .toMutableList()
        if (minions > 0) {
            evilSpecial += if (minions == 1) "Minion" else "Minion x$minions"
        }
        return evilSpecial.joinToString(", ")
    }
}
