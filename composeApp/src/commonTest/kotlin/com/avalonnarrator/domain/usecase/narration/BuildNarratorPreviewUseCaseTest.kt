package com.avalonnarrator.domain.usecase.narration

import com.avalonnarrator.domain.audio.ClipId
import com.avalonnarrator.domain.audio.VoicePackIds
import com.avalonnarrator.domain.model.NarratorTimelineBlock
import com.avalonnarrator.domain.narration.NarrationPlan
import com.avalonnarrator.domain.narration.PlannedClip
import com.avalonnarrator.domain.narration.PlannedStep
import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.engine.rules.RulePhase
import com.avalonnarrator.playback.PlaybackState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BuildNarratorPreviewUseCaseTest {

    private val buildPreview = BuildNarratorPreviewUseCase()

    @Test
    fun `builds deterministic timeline and now-playing text`() {
        val config = GameSetupConfig(
            selectedRoles = setOf(RoleId.MERLIN, RoleId.ASSASSIN),
            loyalServantAdjustment = 2,
            minionAdjustment = 1,
        )
        val plan = NarrationPlan(
            voicePackId = VoicePackIds.WIZARD,
            steps = listOf(
                PlannedStep(
                    stepId = "intro",
                    phase = RulePhase.PRELUDE,
                    clips = listOf(PlannedClip(ClipId.INTRO)),
                    delayAfterMs = 500L,
                ),
            ),
            totalEstimatedDurationMs = 1500L,
        )
        val playback = PlaybackState(
            isPlaying = true,
            currentStepIndex = 0,
            currentClipIndex = 0,
            isInDelay = false,
        )

        val preview = buildPreview(config = config, plan = plan, playbackState = playback)

        assertEquals("1s", preview.estimatedLengthLabel)
        assertTrue(preview.nowPlayingText.contains("Welcome to Avalon."))
        assertEquals(5, preview.selectedTotal)
        assertEquals(2, preview.timelineBlocks.size)
        assertTrue(preview.timelineBlocks.first() is NarratorTimelineBlock.Info)
        assertTrue(preview.timelineBlocks.last() is NarratorTimelineBlock.Pause)
    }
}
