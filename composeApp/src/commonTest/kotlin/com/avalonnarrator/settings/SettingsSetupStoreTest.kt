package com.avalonnarrator.settings

import com.avalonnarrator.domain.audio.VoicePackIds
import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.GameModule
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SettingsSetupStoreTest {

    @Test
    fun `persists and restores setup config`() = runTest {
        val store = SettingsSetupStore(MapSettings())

        val config = GameSetupConfig(
            playerCount = 8,
            selectedRoles = setOf(RoleId.MERLIN, RoleId.PERCIVAL, RoleId.ASSASSIN),
            loyalServantAdjustment = 2,
            minionAdjustment = -1,
            validatorsEnabled = false,
            enabledModules = setOf(GameModule.EXCALIBUR),
            regularPauseMs = 1_500,
            actionPauseMs = 5_000,
            selectedVoicePack = VoicePackIds.WIZARD,
            narrationRemindersEnabled = true,
        )

        store.save(config)
        val restored = store.loadLast()

        assertNotNull(restored)
        assertEquals(config, restored)
    }
}
