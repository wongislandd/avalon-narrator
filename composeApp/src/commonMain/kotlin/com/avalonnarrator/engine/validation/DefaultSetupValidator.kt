package com.avalonnarrator.engine.validation

import com.avalonnarrator.domain.roles.Alignment
import com.avalonnarrator.domain.model.SetupIssueCode
import com.avalonnarrator.domain.roles.RoleCatalog
import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.domain.setup.RosterBuilder
import com.avalonnarrator.domain.setup.SetupIssue
import com.avalonnarrator.domain.setup.SetupIssueLevel

class DefaultSetupValidator : SetupValidator {

    override fun validate(config: GameSetupConfig): List<SetupIssue> {
        val issues = mutableListOf<SetupIssue>()
        val roster = RosterBuilder.build(config)
        val effectiveRoles = roster.effectiveRoles

        if (RoleId.PERCIVAL in effectiveRoles && RoleId.MERLIN !in effectiveRoles) {
            issues += SetupIssue(
                level = SetupIssueLevel.WARNING,
                code = SetupIssueCode.PERCIVAL_WITHOUT_MERLIN,
                message = "Percival is selected without Merlin, so Percival loses core information value.",
                affectedRoles = setOf(RoleId.PERCIVAL, RoleId.MERLIN),
            )
        }

        if (RoleId.MORGANA in effectiveRoles && RoleId.PERCIVAL !in effectiveRoles) {
            issues += SetupIssue(
                level = SetupIssueLevel.WARNING,
                code = SetupIssueCode.MORGANA_WITHOUT_PERCIVAL,
                message = "Morgana is selected without Percival, reducing Morgana's deception impact.",
                affectedRoles = setOf(RoleId.MORGANA, RoleId.PERCIVAL),
            )
        }

        if (RoleId.MERLIN in effectiveRoles && RoleId.ASSASSIN !in effectiveRoles) {
            issues += SetupIssue(
                level = SetupIssueLevel.WARNING,
                code = SetupIssueCode.MERLIN_WITHOUT_ASSASSIN,
                message = "Merlin is selected without Assassin, lowering late-game tension.",
                affectedRoles = setOf(RoleId.MERLIN, RoleId.ASSASSIN),
            )
        }

        val hasGoodLancelot = RoleId.LANCELOT_GOOD in effectiveRoles
        val hasEvilLancelot = RoleId.LANCELOT_EVIL in effectiveRoles
        if (hasGoodLancelot.xor(hasEvilLancelot)) {
            issues += SetupIssue(
                level = SetupIssueLevel.ERROR,
                code = SetupIssueCode.LANCELOT_PAIR_REQUIRED,
                message = "Lancelot roles must be used as a pair (Good Lancelot + Evil Lancelot).",
                affectedRoles = setOf(RoleId.LANCELOT_GOOD, RoleId.LANCELOT_EVIL),
            )
        }

        val hasSeniorMessenger = RoleId.SENIOR_MESSENGER in effectiveRoles
        val hasJuniorMessenger = RoleId.JUNIOR_MESSENGER in effectiveRoles
        val hasEvilMessenger = RoleId.EVIL_MESSENGER in effectiveRoles
        val messengerCount = listOf(hasSeniorMessenger, hasJuniorMessenger, hasEvilMessenger).count { it }
        if (messengerCount in 1..2) {
            issues += SetupIssue(
                level = SetupIssueLevel.ERROR,
                code = SetupIssueCode.MESSENGER_TRIO_REQUIRED,
                message = "Messenger module requires all three roles: Senior Messenger, Junior Messenger, and Evil Messenger.",
                affectedRoles = setOf(RoleId.SENIOR_MESSENGER, RoleId.JUNIOR_MESSENGER, RoleId.EVIL_MESSENGER),
            )
        }

        val hasGoodRogue = RoleId.ROGUE_GOOD in effectiveRoles
        val hasEvilRogue = RoleId.ROGUE_EVIL in effectiveRoles
        if (hasGoodRogue.xor(hasEvilRogue)) {
            issues += SetupIssue(
                level = SetupIssueLevel.ERROR,
                code = SetupIssueCode.ROGUE_PAIR_REQUIRED,
                message = "Rogue module requires both roles: Good Rogue and Evil Rogue.",
                affectedRoles = setOf(RoleId.ROGUE_GOOD, RoleId.ROGUE_EVIL),
            )
        }

        val hasGoodSorcerer = RoleId.SORCERER_GOOD in effectiveRoles
        val hasEvilSorcerer = RoleId.SORCERER_EVIL in effectiveRoles
        if (hasGoodSorcerer.xor(hasEvilSorcerer)) {
            issues += SetupIssue(
                level = SetupIssueLevel.ERROR,
                code = SetupIssueCode.SORCERER_PAIR_REQUIRED,
                message = "Sorcerer module requires both roles: Good Sorcerer and Evil Sorcerer.",
                affectedRoles = setOf(RoleId.SORCERER_GOOD, RoleId.SORCERER_EVIL),
            )
        }

        val specialEvilCount = roster.selectedSpecialRoles.count { roleId ->
            RoleCatalog.byId(roleId)?.alignment == Alignment.EVIL
        }
        val evilCount = specialEvilCount + roster.minionCount
        val totalAssigned = roster.selectedSpecialRoles.size + roster.loyalServantCount + roster.minionCount
        val goodCount = totalAssigned - evilCount
        val expectedEvilCount = RosterBuilder.expectedEvilCount(totalAssigned)
        if (expectedEvilCount != null && evilCount != expectedEvilCount) {
            val expectedGoodCount = totalAssigned - expectedEvilCount
            issues += SetupIssue(
                level = SetupIssueLevel.ERROR,
                code = SetupIssueCode.TEAM_RATIO_INVALID,
                message = "Invalid team ratio for $totalAssigned players: selected $goodCount good / $evilCount evil, expected $expectedGoodCount good / $expectedEvilCount evil.",
            )
        }

        if (totalAssigned < 5) {
            issues += SetupIssue(
                level = SetupIssueLevel.ERROR,
                code = SetupIssueCode.MIN_PLAYERS_NOT_MET,
                message = "Select at least 5 characters to start a game.",
            )
        }

        if (totalAssigned > 10) {
            issues += SetupIssue(
                level = SetupIssueLevel.ERROR,
                code = SetupIssueCode.MAX_PLAYERS_EXCEEDED,
                message = "Avalon supports up to 10 players.",
            )
        }

        return issues
    }
}
