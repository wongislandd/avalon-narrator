package com.avalonnarrator.domain.usecase.setup

import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.GameModule
import com.avalonnarrator.domain.setup.GameSetupConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MutateSetupUseCaseTest {

    private val mutate = MutateSetupUseCase()

    @Test
    fun `toggle role updates selected set and derived player count`() {
        val initial = GameSetupConfig(
            selectedRoles = setOf(RoleId.MERLIN, RoleId.ASSASSIN),
            loyalServantAdjustment = 2,
            minionAdjustment = 1,
        )

        val withPercival = mutate(initial, SetupMutation.ToggleRole(RoleId.PERCIVAL))
        val withoutPercival = mutate(withPercival, SetupMutation.ToggleRole(RoleId.PERCIVAL))

        assertTrue(RoleId.PERCIVAL in withPercival.selectedRoles)
        assertEquals(6, withPercival.playerCount)
        assertTrue(RoleId.PERCIVAL !in withoutPercival.selectedRoles)
        assertEquals(5, withoutPercival.playerCount)
    }

    @Test
    fun `base roles are not mutated through special-role toggle event`() {
        val initial = GameSetupConfig()

        val loyalToggleAttempt = mutate(initial, SetupMutation.ToggleRole(RoleId.LOYAL_SERVANT))
        val minionToggleAttempt = mutate(initial, SetupMutation.ToggleRole(RoleId.MINION))

        assertEquals(initial, loyalToggleAttempt)
        assertEquals(initial, minionToggleAttempt)
    }

    @Test
    fun `lancelot module is inferred from selected lancelot roles`() {
        val initial = GameSetupConfig(selectedRoles = setOf(RoleId.MERLIN, RoleId.ASSASSIN))

        val mutated = mutate(initial, SetupMutation.ToggleRole(RoleId.LANCELOT_GOOD))

        assertTrue(RoleId.LANCELOT_GOOD in mutated.selectedRoles)
        assertTrue(GameModule.LANCELOT in mutated.enabledModules)
    }

    @Test
    fun `base role quantities are clamped and player count is re-derived`() {
        val initial = GameSetupConfig(
            selectedRoles = setOf(RoleId.MERLIN, RoleId.ASSASSIN),
            loyalServantAdjustment = 2,
            minionAdjustment = 1,
        )

        val withHighLoyal = mutate(initial, SetupMutation.SetLoyalServantCount(99))
        val withNegativeMinion = mutate(withHighLoyal, SetupMutation.SetMinionCount(-3))

        assertEquals(12, withHighLoyal.loyalServantAdjustment)
        assertEquals(15, withHighLoyal.playerCount)
        assertEquals(0, withNegativeMinion.minionAdjustment)
        assertEquals(14, withNegativeMinion.playerCount)
    }
}
