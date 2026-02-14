package com.avalonnarrator.presentation.narrator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avalonnarrator.app.di.NarrationSession
import com.avalonnarrator.app.di.SetupSession
import com.avalonnarrator.domain.usecase.narration.BuildNarratorPreviewUseCase
import com.avalonnarrator.navigation.AppScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NarratorViewModel(
    private val setupSession: SetupSession,
    private val narrationSession: NarrationSession,
    private val buildNarratorPreviewUseCase: BuildNarratorPreviewUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NarratorUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<NarratorUiEffect>()
    val effects = _effects.asSharedFlow()

    init {
        observeNarratorState()
    }

    fun onEvent(event: NarratorUiEvent) {
        when (event) {
            NarratorUiEvent.PlayPause -> narrationSession.playOrPause()
            NarratorUiEvent.Restart -> narrationSession.restart()
            NarratorUiEvent.NextStep -> narrationSession.nextStep()
            NarratorUiEvent.Back -> {
                narrationSession.pause()
                viewModelScope.launch {
                    _effects.emit(NarratorUiEffect.Navigate(AppScreen.SETUP))
                }
            }
        }
    }

    private fun observeNarratorState() {
        viewModelScope.launch {
            combine(
                setupSession.config,
                setupSession.isInitialized,
                narrationSession.plan,
                narrationSession.playbackState,
            ) { config, initialized, plan, playback ->
                val preview = buildNarratorPreviewUseCase(
                    config = config,
                    plan = plan,
                    playbackState = playback,
                )
                NarratorUiState(
                    isInitialized = initialized,
                    config = config,
                    narrationPlan = plan,
                    playbackState = playback,
                    preview = preview,
                )
            }.collectLatest { nextState ->
                _uiState.update { nextState }
            }
        }
    }
}
