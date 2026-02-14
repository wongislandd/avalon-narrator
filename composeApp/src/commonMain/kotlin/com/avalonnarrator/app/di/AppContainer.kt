package com.avalonnarrator.app.di

import com.avalonnarrator.domain.usecase.narration.BuildNarrationPlanUseCase
import com.avalonnarrator.domain.usecase.narration.BuildNarratorPreviewUseCase
import com.avalonnarrator.domain.usecase.setup.DerivePlayerCountUseCase
import com.avalonnarrator.domain.usecase.setup.MutateSetupUseCase
import com.avalonnarrator.domain.usecase.setup.ValidateSetupUseCase
import com.avalonnarrator.engine.planner.DefaultNarrationPlanner
import com.avalonnarrator.engine.planner.NarrationPlanner
import com.avalonnarrator.engine.validation.DefaultSetupValidator
import com.avalonnarrator.engine.validation.SetupValidator
import com.avalonnarrator.playback.DefaultClipResolver
import com.avalonnarrator.playback.DefaultNarrationPlayer
import com.avalonnarrator.playback.NarrationPlayer
import com.avalonnarrator.settings.SettingsSetupStore
import com.avalonnarrator.settings.SetupStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AppContainer(
    private val appScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    setupStore: SetupStore = SettingsSetupStore(),
    setupValidator: SetupValidator = DefaultSetupValidator(),
    narrationPlanner: NarrationPlanner = DefaultNarrationPlanner(),
    narrationPlayer: NarrationPlayer = DefaultNarrationPlayer(DefaultClipResolver()),
) {
    private val derivePlayerCountUseCase = DerivePlayerCountUseCase()
    private val mutateSetupUseCase = MutateSetupUseCase(derivePlayerCount = derivePlayerCountUseCase)

    val setupSession = SetupSession(
        setupStore = setupStore,
        mutateSetupUseCase = mutateSetupUseCase,
        scope = appScope,
    )
    val narrationSession = NarrationSession(narrationPlayer)

    val validateSetupUseCase = ValidateSetupUseCase(setupValidator = setupValidator)
    val buildNarrationPlanUseCase = BuildNarrationPlanUseCase(narrationPlanner = narrationPlanner)
    val buildNarratorPreviewUseCase = BuildNarratorPreviewUseCase()
}
