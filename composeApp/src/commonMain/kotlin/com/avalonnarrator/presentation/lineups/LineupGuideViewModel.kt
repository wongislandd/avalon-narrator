package com.avalonnarrator.presentation.lineups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avalonnarrator.app.di.SetupSession
import com.avalonnarrator.domain.recommendation.RecommendedLineupCatalog
import com.avalonnarrator.domain.usecase.setup.SetupMutation
import com.avalonnarrator.navigation.AppScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LineupGuideViewModel(
    private val setupSession: SetupSession,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LineupGuideUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<LineupGuideUiEffect>()
    val effects = _effects.asSharedFlow()

    private var initializedFromSetup = false

    init {
        setupSession.initialize()
        observeSetup()
    }

    fun onEvent(event: LineupGuideUiEvent) {
        when (event) {
            LineupGuideUiEvent.IncreasePlayers -> {
                updatePlayerCount(_uiState.value.selectedPlayerCount + 1)
            }

            LineupGuideUiEvent.DecreasePlayers -> {
                updatePlayerCount(_uiState.value.selectedPlayerCount - 1)
            }

            is LineupGuideUiEvent.ApplyLineup -> {
                setupSession.mutate(
                    SetupMutation.ApplyRecommendedLineup(
                        specialRoles = event.lineup.specialRoles,
                        loyalServantCount = event.lineup.loyalServants,
                        minionCount = event.lineup.minions,
                        enabledModules = event.lineup.recommendedModules,
                    ),
                )
                viewModelScope.launch {
                    _effects.emit(LineupGuideUiEffect.Navigate(AppScreen.SETUP))
                }
            }

            LineupGuideUiEvent.Back -> {
                viewModelScope.launch {
                    _effects.emit(LineupGuideUiEffect.Navigate(AppScreen.SETUP))
                }
            }
        }
    }

    private fun observeSetup() {
        viewModelScope.launch {
            setupSession.isInitialized.collectLatest { isInitialized ->
                if (!initializedFromSetup && isInitialized) {
                    initializedFromSetup = true
                    _uiState.update { state -> state.copy(isInitialized = true) }
                } else {
                    _uiState.update { it.copy(isInitialized = isInitialized) }
                }
            }
        }
    }

    private fun updatePlayerCount(rawCount: Int) {
        val playerCount = rawCount.coerceIn(5, 10)
        _uiState.update {
            it.copy(
                isInitialized = true,
                selectedPlayerCount = playerCount,
                lineups = RecommendedLineupCatalog.forPlayerCount(playerCount),
            )
        }
    }
}
