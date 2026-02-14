package com.avalonnarrator.settings

import com.avalonnarrator.domain.setup.GameSetupConfig

interface SetupStore {
    suspend fun loadLast(): GameSetupConfig?
    suspend fun save(config: GameSetupConfig)
}
