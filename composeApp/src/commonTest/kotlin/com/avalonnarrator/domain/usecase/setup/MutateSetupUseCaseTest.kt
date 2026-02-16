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

    @Test
    fun `apply recommended lineup replaces roster and re-derives modules`() {
        val initial = GameSetupConfig(
            selectedRoles = setOf(RoleId.MERLIN, RoleId.ASSASSIN, RoleId.PERCIVAL),
            loyalServantAdjustment = 2,
            minionAdjustment = 1,
            enabledModules = setOf(GameModule.EXCALIBUR),
        )

        val applied = mutate(
            initial,
            SetupMutation.ApplyRecommendedLineup(
                specialRoles = setOf(
                    RoleId.MERLIN,
                    RoleId.PERCIVAL,
                    RoleId.LANCELOT_GOOD,
                    RoleId.ASSASSIN,
                    RoleId.MORGANA,
                    RoleId.LANCELOT_EVIL,
                ),
                loyalServantCount = 2,
                minionCount = 0,
            ),
        )

        assertEquals(
            setOf(
                RoleId.MERLIN,
                RoleId.PERCIVAL,
                RoleId.LANCELOT_GOOD,
                RoleId.ASSASSIN,
                RoleId.MORGANA,
                RoleId.LANCELOT_EVIL,
            ),
            applied.selectedRoles,
        )
        assertEquals(2, applied.loyalServantAdjustment)
        assertEquals(0, applied.minionAdjustment)
        assertEquals(8, applied.playerCount)
        assertTrue(GameModule.LANCELOT in applied.enabledModules)
    }

    @Test
    fun `pause mutations are clamped and applied`() {
        val initial = GameSetupConfig()

        val withRegular = mutate(initial, SetupMutation.SetRegularPauseMs(1_500))
        val withAction = mutate(withRegular, SetupMutation.SetActionPauseMs(20_000))
        val withLowRegular = mutate(withAction, SetupMutation.SetRegularPauseMs(-1_000))

        assertEquals(1_500, withRegular.regularPauseMs)
        assertEquals(15_000, withAction.actionPauseMs)
        assertEquals(0, withLowRegular.regularPauseMs)
    }
}
