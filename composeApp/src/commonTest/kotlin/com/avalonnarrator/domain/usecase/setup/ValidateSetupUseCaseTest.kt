package com.avalonnarrator.domain.usecase.setup

import com.avalonnarrator.domain.model.SetupIssueCode
import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.engine.validation.DefaultSetupValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidateSetupUseCaseTest {

    private val validate = ValidateSetupUseCase(DefaultSetupValidator())

    @Test
    fun `returns no issues when validators are disabled`() {
        val result = validate(
            GameSetupConfig(
                validatorsEnabled = false,
                selectedRoles = emptySet(),
                loyalServantAdjustment = 0,
                minionAdjustment = 0,
            ),
        )

        assertTrue(result.allIssues.isEmpty())
        assertTrue(result.blockingIssues.isEmpty())
        assertTrue(result.nonBlockingIssues.isEmpty())
        assertTrue(result.canStart)
    }

    @Test
    fun `splits blocking and non blocking issues with typed codes`() {
        val result = validate(
            GameSetupConfig(
                validatorsEnabled = true,
                selectedRoles = setOf(RoleId.MERLIN),
                loyalServantAdjustment = 0,
                minionAdjustment = 0,
            ),
        )

        assertFalse(result.canStart)
        assertTrue(result.blockingIssues.any { it.code == SetupIssueCode.MIN_PLAYERS_NOT_MET })
        assertTrue(result.nonBlockingIssues.any { it.code == SetupIssueCode.MERLIN_WITHOUT_ASSASSIN })
        assertEquals(result.allIssues.size, result.blockingIssues.size + result.nonBlockingIssues.size)
    }
}
