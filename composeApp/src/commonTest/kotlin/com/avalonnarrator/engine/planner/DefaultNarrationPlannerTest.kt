package com.avalonnarrator.engine.planner

import com.avalonnarrator.domain.audio.ClipId
import com.avalonnarrator.domain.audio.VoicePackIds
import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.domain.setup.GameModule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultNarrationPlannerTest {

    @Test
    fun `plans steps in phase-order sequence`() {
        val planner = DefaultNarrationPlanner()
        val config = GameSetupConfig(
            selectedRoles = setOf(RoleId.MERLIN, RoleId.ASSASSIN, RoleId.PERCIVAL),
            selectedVoicePack = VoicePackIds.WIZARD,
        )

        val plan = planner.plan(config)

        assertTrue(plan.steps.isNotEmpty())
        assertTrue(plan.steps.first().stepId.startsWith("intro"))
        assertTrue(plan.steps.last().stepId.startsWith("closing"))
        assertEquals(VoicePackIds.WIZARD, plan.voicePackId)
    }

    @Test
    fun `includes module step when enabled`() {
        val planner = DefaultNarrationPlanner()

        val plan = planner.plan(
            GameSetupConfig(
                enabledModules = setOf(GameModule.EXCALIBUR),
            ),
        )

        val stepIds = plan.steps.map { it.stepId }
        assertTrue("excalibur_module" in stepIds)
        val moduleStep = plan.steps.first { it.stepId == "excalibur_module" }
        assertEquals(ClipId.EXCALIBUR, moduleStep.clips.single().clipId)
    }

    @Test
    fun `infers lancelot module from selected lancelot roles`() {
        val planner = DefaultNarrationPlanner()

        val plan = planner.plan(
            GameSetupConfig(
                selectedRoles = setOf(RoleId.LANCELOT_GOOD, RoleId.LANCELOT_EVIL, RoleId.ASSASSIN),
            ),
        )

        assertTrue(plan.steps.any { it.stepId.startsWith("lancelot_counterpart") })
    }

    @Test
    fun `does not include reminders by default`() {
        val planner = DefaultNarrationPlanner()

        val plan = planner.plan(
            GameSetupConfig(
                selectedRoles = setOf(RoleId.MERLIN, RoleId.ASSASSIN),
            ),
        )

        assertTrue(plan.steps.none { it.stepId.startsWith("reminder_") })
    }

    @Test
    fun `includes reminders when enabled in config`() {
        val planner = DefaultNarrationPlanner()

        val plan = planner.plan(
            GameSetupConfig(
                selectedRoles = setOf(RoleId.MERLIN, RoleId.ASSASSIN),
                narrationRemindersEnabled = true,
            ),
        )

        assertTrue("reminder_merlin" in plan.steps.map { it.stepId })
        assertTrue("reminder_assassin" in plan.steps.map { it.stepId })
    }

    @Test
    fun `splits multi line steps into separate planned blocks`() {
        val planner = DefaultNarrationPlanner()

        val plan = planner.plan(
            GameSetupConfig(
                selectedRoles = setOf(RoleId.CLERIC, RoleId.ASSASSIN),
            ),
        )

        val clericLines = plan.steps.filter { it.stepId.startsWith("cleric_alignment_check.line") }
        assertEquals(4, clericLines.size)
        assertTrue(clericLines.all { it.clips.size == 1 })
    }

    @Test
    fun `uses hidden rogue evil-info line when evil rogue is selected`() {
        val planner = DefaultNarrationPlanner()

        val plan = planner.plan(
            GameSetupConfig(
                selectedRoles = setOf(RoleId.ASSASSIN, RoleId.ROGUE_GOOD, RoleId.ROGUE_EVIL),
            ),
        )

        val clipIds = plan.steps.flatMap { step -> step.clips.map { it.clipId } }
        assertTrue(ClipId.EVIL_WAKE_EXCEPT_EVIL_ROGUE in clipIds)
        assertTrue(ClipId.EVIL_WAKE !in clipIds)
    }

    @Test
    fun `uses hidden sorcerer merlin-info lines when evil sorcerer is selected`() {
        val planner = DefaultNarrationPlanner()

        val plan = planner.plan(
            GameSetupConfig(
                selectedRoles = setOf(RoleId.MERLIN, RoleId.ASSASSIN, RoleId.SORCERER_GOOD, RoleId.SORCERER_EVIL),
            ),
        )

        val clipIds = plan.steps.flatMap { step -> step.clips.map { it.clipId } }
        assertTrue(ClipId.MINIONS_EXTEND_THUMB_FOR_MERLIN_EXCEPT_EVIL_SORCERER in clipIds)
        assertTrue(ClipId.MINIONS_EXTEND_THUMB_FOR_MERLIN !in clipIds)
    }

    @Test
    fun `same setup with different voice packs yields same clip sequence`() {
        val planner = DefaultNarrationPlanner()
        val baseConfig = GameSetupConfig(
            selectedRoles = setOf(RoleId.MERLIN, RoleId.PERCIVAL, RoleId.ASSASSIN, RoleId.MORGANA),
            loyalServantAdjustment = 2,
            minionAdjustment = 1,
        )

        val wizard = planner.plan(baseConfig.copy(selectedVoicePack = VoicePackIds.WIZARD))
        val rainbird = planner.plan(baseConfig.copy(selectedVoicePack = VoicePackIds.RAINBIRD_EN))

        assertEquals(
            wizard.steps.flatMap { step -> step.clips.map { it.clipId } },
            rainbird.steps.flatMap { step -> step.clips.map { it.clipId } },
        )
        assertEquals(VoicePackIds.WIZARD, wizard.voicePackId)
        assertEquals(VoicePackIds.RAINBIRD_EN, rainbird.voicePackId)
    }

    @Test
    fun `uses regular and action pause categories from config`() {
        val planner = DefaultNarrationPlanner()
        val config = GameSetupConfig(
            selectedRoles = setOf(RoleId.MERLIN, RoleId.ASSASSIN),
            regularPauseMs = 1_000,
            actionPauseMs = 4_000,
        )

        val plan = planner.plan(config)

        val introFirstLine = plan.steps.first { it.stepId == "intro.line1" }
        val evilInfoFirstLine = plan.steps.first { it.stepId == "evil_info.line1" }

        assertEquals(1_000L, introFirstLine.delayAfterMs)
        assertEquals(4_000L, evilInfoFirstLine.delayAfterMs)
    }

    @Test
    fun `uses action pause only for inspection windows and regular for quick actions`() {
        val planner = DefaultNarrationPlanner()
        val config = GameSetupConfig(
            selectedRoles = setOf(RoleId.CLERIC, RoleId.ASSASSIN),
            regularPauseMs = 1_000,
            actionPauseMs = 4_000,
        )

        val plan = planner.plan(config)

        val clericThumb = plan.steps.first { it.stepId == "cleric_alignment_check.line1" }
        val clericOpenEyes = plan.steps.first { it.stepId == "cleric_alignment_check.line2" }
        val clericCloseEyes = plan.steps.first { it.stepId == "cleric_alignment_check.line3" }
        val leaderReturnFist = plan.steps.first { it.stepId == "cleric_alignment_check.line4" }

        assertEquals(1_000L, clericThumb.delayAfterMs)
        assertEquals(4_000L, clericOpenEyes.delayAfterMs)
        assertEquals(1_000L, clericCloseEyes.delayAfterMs)
        assertEquals(1_000L, leaderReturnFist.delayAfterMs)
    }
}
