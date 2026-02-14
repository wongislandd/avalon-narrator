package com.avalonnarrator.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.avalonnarrator.app.di.AppContainer
import com.avalonnarrator.navigation.AppNavigatorState
import com.avalonnarrator.navigation.AppScreen
import com.avalonnarrator.navigation.PlatformBackHandler
import com.avalonnarrator.presentation.narrator.NarratorUiEffect
import com.avalonnarrator.presentation.narrator.NarratorUiEvent
import com.avalonnarrator.presentation.narrator.NarratorViewModel
import com.avalonnarrator.presentation.settings.SettingsUiEffect
import com.avalonnarrator.presentation.settings.SettingsUiEvent
import com.avalonnarrator.presentation.settings.SettingsViewModel
import com.avalonnarrator.presentation.setup.SetupUiEffect
import com.avalonnarrator.presentation.setup.SetupViewModel
import com.avalonnarrator.ui.screens.NarratorScreen
import com.avalonnarrator.ui.screens.SettingsScreen
import com.avalonnarrator.ui.screens.SetupScreen
import com.avalonnarrator.ui.screens.VoicePackSelectionScreen
import com.avalonnarrator.ui.theme.AvalonTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AvalonNarratorApp() {
    val container = remember { AppContainer() }
    val navigator = remember { AppNavigatorState() }

    val setupViewModel = remember(container) {
        SetupViewModel(
            setupSession = container.setupSession,
            narrationSession = container.narrationSession,
            validateSetupUseCase = container.validateSetupUseCase,
            buildNarrationPlanUseCase = container.buildNarrationPlanUseCase,
        )
    }
    val settingsViewModel = remember(container) {
        SettingsViewModel(setupSession = container.setupSession)
    }
    val narratorViewModel = remember(container) {
        NarratorViewModel(
            setupSession = container.setupSession,
            narrationSession = container.narrationSession,
            buildNarratorPreviewUseCase = container.buildNarratorPreviewUseCase,
        )
    }

    val setupUiState by setupViewModel.uiState.collectAsState()
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    val narratorUiState by narratorViewModel.uiState.collectAsState()

    LaunchedEffect(setupViewModel) {
        setupViewModel.effects.collectLatest { effect ->
            when (effect) {
                is SetupUiEffect.Navigate -> navigator.navigate(effect.screen)
            }
        }
    }

    LaunchedEffect(settingsViewModel) {
        settingsViewModel.effects.collectLatest { effect ->
            when (effect) {
                is SettingsUiEffect.Navigate -> navigator.navigate(effect.screen)
            }
        }
    }

    LaunchedEffect(narratorViewModel) {
        narratorViewModel.effects.collectLatest { effect ->
            when (effect) {
                is NarratorUiEffect.Navigate -> navigator.navigate(effect.screen)
            }
        }
    }

    AvalonTheme {
        PlatformBackHandler(
            enabled = navigator.currentScreen != AppScreen.SETUP,
            onBack = {
                when (navigator.currentScreen) {
                    AppScreen.SETUP -> Unit
                    AppScreen.SETTINGS -> settingsViewModel.onEvent(SettingsUiEvent.Back)
                    AppScreen.VOICE_SELECTION -> settingsViewModel.onEvent(SettingsUiEvent.CloseVoiceSelection)
                    AppScreen.NARRATOR -> narratorViewModel.onEvent(NarratorUiEvent.Back)
                }
            },
        )

        when (navigator.currentScreen) {
            AppScreen.SETUP -> SetupScreen(
                uiState = setupUiState,
                onEvent = setupViewModel::onEvent,
            )

            AppScreen.SETTINGS -> SettingsScreen(
                uiState = settingsUiState,
                onEvent = settingsViewModel::onEvent,
            )

            AppScreen.VOICE_SELECTION -> VoicePackSelectionScreen(
                uiState = settingsUiState,
                onEvent = settingsViewModel::onEvent,
            )

            AppScreen.NARRATOR -> NarratorScreen(
                uiState = narratorUiState,
                onEvent = narratorViewModel::onEvent,
            )
        }
    }
}
