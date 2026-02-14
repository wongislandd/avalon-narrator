package com.avalonnarrator.engine.validation

import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.model.SetupIssueCode
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.domain.setup.SetupIssueLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultSetupValidatorTest {

    private val validator = DefaultSetupValidator()

    @Test
    fun `warns when percival selected without merlin`() {
        val issues = validator.validate(
            GameSetupConfig(selectedRoles = setOf(RoleId.PERCIVAL, RoleId.ASSASSIN)),
        )

        val issue = issues.firstOrNull { it.code == SetupIssueCode.PERCIVAL_WITHOUT_MERLIN }
        assertTrue(issue != null)
        assertEquals(SetupIssueLevel.WARNING, issue.level)
    }

    @Test
    fun `blocks when only one lancelot role is selected`() {
        val issues = validator.validate(
            GameSetupConfig(selectedRoles = setOf(RoleId.LANCELOT_GOOD, RoleId.ASSASSIN)),
        )

        val issue = issues.firstOrNull { it.code == SetupIssueCode.LANCELOT_PAIR_REQUIRED }
        assertTrue(issue != null)
        assertEquals(SetupIssueLevel.ERROR, issue.level)
    }

    @Test
    fun `blocks when messenger trio is incomplete`() {
        val issues = validator.validate(
            GameSetupConfig(selectedRoles = setOf(RoleId.SENIOR_MESSENGER, RoleId.ASSASSIN)),
        )

        val issue = issues.firstOrNull { it.code == SetupIssueCode.MESSENGER_TRIO_REQUIRED }
        assertTrue(issue != null)
        assertEquals(SetupIssueLevel.ERROR, issue.level)
    }

    @Test
    fun `blocks when rogue pair is incomplete`() {
        val issues = validator.validate(
            GameSetupConfig(selectedRoles = setOf(RoleId.ROGUE_EVIL, RoleId.ASSASSIN)),
        )

        val issue = issues.firstOrNull { it.code == SetupIssueCode.ROGUE_PAIR_REQUIRED }
        assertTrue(issue != null)
        assertEquals(SetupIssueLevel.ERROR, issue.level)
    }

    @Test
    fun `blocks when sorcerer pair is incomplete`() {
        val issues = validator.validate(
            GameSetupConfig(selectedRoles = setOf(RoleId.SORCERER_EVIL, RoleId.ASSASSIN)),
        )

        val issue = issues.firstOrNull { it.code == SetupIssueCode.SORCERER_PAIR_REQUIRED }
        assertTrue(issue != null)
        assertEquals(SetupIssueLevel.ERROR, issue.level)
    }

    @Test
    fun `does not block when messenger trio and role pairs are complete`() {
        val issues = validator.validate(
            GameSetupConfig(
                selectedRoles = setOf(
                    RoleId.SENIOR_MESSENGER,
                    RoleId.JUNIOR_MESSENGER,
                    RoleId.EVIL_MESSENGER,
                    RoleId.ROGUE_GOOD,
                    RoleId.ROGUE_EVIL,
                    RoleId.SORCERER_GOOD,
                    RoleId.SORCERER_EVIL,
                    RoleId.MERLIN,
                    RoleId.ASSASSIN,
                ),
            ),
        )

        assertTrue(issues.none { it.code == SetupIssueCode.MESSENGER_TRIO_REQUIRED })
        assertTrue(issues.none { it.code == SetupIssueCode.ROGUE_PAIR_REQUIRED })
        assertTrue(issues.none { it.code == SetupIssueCode.SORCERER_PAIR_REQUIRED })
    }

    @Test
    fun `blocks when fewer than 5 characters are selected`() {
        val issues = validator.validate(
            GameSetupConfig(
                playerCount = 10,
                selectedRoles = emptySet(),
                loyalServantAdjustment = -5,
                minionAdjustment = -4,
            ),
        )

        val issue = issues.firstOrNull { it.code == SetupIssueCode.MIN_PLAYERS_NOT_MET }
        assertTrue(issue != null)
        assertEquals(SetupIssueLevel.ERROR, issue.level)
    }

    @Test
    fun `blocks when selected character count exceeds 10`() {
        val issues = validator.validate(
            GameSetupConfig(
                selectedRoles = setOf(
                    RoleId.MERLIN,
                    RoleId.PERCIVAL,
                    RoleId.ASSASSIN,
                    RoleId.MORGANA,
                    RoleId.MORDRED,
                    RoleId.OBERON,
                    RoleId.CLERIC,
                    RoleId.SORCERER_GOOD,
                ),
                loyalServantAdjustment = 2,
                minionAdjustment = 1,
            ),
        )

        val issue = issues.firstOrNull { it.code == SetupIssueCode.MAX_PLAYERS_EXCEEDED }
        assertTrue(issue != null)
        assertEquals(SetupIssueLevel.ERROR, issue.level)
    }

    @Test
    fun `blocks when good evil ratio is invalid for selected total`() {
        val issues = validator.validate(
            GameSetupConfig(
                selectedRoles = setOf(RoleId.MERLIN, RoleId.ASSASSIN, RoleId.MORGANA),
                loyalServantAdjustment = 2,
                minionAdjustment = 1,
            ),
        )

        val issue = issues.firstOrNull { it.code == SetupIssueCode.TEAM_RATIO_INVALID }
        assertTrue(issue != null)
        assertEquals(SetupIssueLevel.ERROR, issue.level)
    }

    @Test
    fun `does not report ratio issue when good evil ratio is valid`() {
        val issues = validator.validate(
            GameSetupConfig(
                selectedRoles = setOf(RoleId.MERLIN, RoleId.PERCIVAL, RoleId.ASSASSIN, RoleId.MORGANA),
                loyalServantAdjustment = 2,
                minionAdjustment = 1,
            ),
        )

        assertTrue(issues.none { it.code == SetupIssueCode.TEAM_RATIO_INVALID })
    }
}
