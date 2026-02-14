package com.avalonnarrator.app.di

import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.domain.usecase.setup.MutateSetupUseCase
import com.avalonnarrator.domain.usecase.setup.SetupMutation
import com.avalonnarrator.settings.SetupStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SetupSession(
    private val setupStore: SetupStore,
    private val mutateSetupUseCase: MutateSetupUseCase,
    private val scope: CoroutineScope,
) {
    private val _config = MutableStateFlow(mutateSetupUseCase.normalize(GameSetupConfig()))
    val config: StateFlow<GameSetupConfig> = _config.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    fun initialize() {
        if (_isInitialized.value) return
        scope.launch {
            val restored = setupStore.loadLast() ?: GameSetupConfig()
            _config.value = mutateSetupUseCase.normalize(restored)
            _isInitialized.value = true
        }
    }

    fun mutate(mutation: SetupMutation) {
        val next = mutateSetupUseCase(_config.value, mutation)
        _config.value = next
        scope.launch {
            setupStore.save(next)
        }
    }
}
