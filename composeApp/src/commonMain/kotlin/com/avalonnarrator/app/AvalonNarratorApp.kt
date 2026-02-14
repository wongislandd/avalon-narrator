package com.avalonnarrator.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.avalonnarrator.navigation.AppScreen
import com.avalonnarrator.navigation.PlatformBackHandler
import com.avalonnarrator.ui.screens.NarratorScreen
import com.avalonnarrator.ui.screens.SettingsScreen
import com.avalonnarrator.ui.screens.SetupScreen
import com.avalonnarrator.ui.theme.AvalonTheme

@Composable
fun AvalonNarratorApp() {
    val controller = remember { AvalonAppController() }
    val uiState by controller.uiState.collectAsState()
    val playbackState by controller.playbackState.collectAsState()

    LaunchedEffect(Unit) {
        controller.initialize()
    }

    AvalonTheme {
        PlatformBackHandler(
            enabled = uiState.screen != AppScreen.SETUP,
            onBack = { controller.navigate(AppScreen.SETUP) },
        )

        when (uiState.screen) {
            AppScreen.SETUP -> SetupScreen(
                uiState = uiState,
                onToggleRole = controller::toggleRole,
                onIncreaseLoyalServants = controller::increaseLoyalServants,
                onDecreaseLoyalServants = controller::decreaseLoyalServants,
                onIncreaseMinions = controller::increaseMinions,
                onDecreaseMinions = controller::decreaseMinions,
                onToggleLoyalServantSelection = controller::toggleLoyalServantSelection,
                onToggleMinionSelection = controller::toggleMinionSelection,
                onOpenSettings = { controller.navigate(AppScreen.SETTINGS) },
                onStartRun = controller::startNarrationRun,
            )

            AppScreen.SETTINGS -> SettingsScreen(
                uiState = uiState,
                onBack = { controller.navigate(AppScreen.SETUP) },
                onToggleModule = controller::toggleModule,
                onValidatorsChanged = controller::setValidatorsEnabled,
                onPaceChanged = controller::setNarrationPace,
                onRegenerateSeed = controller::regenerateSeed,
                onVoicePackChanged = controller::setVoicePack,
                onNarrationRemindersChanged = controller::setNarrationReminders,
                onDebugTimelineChanged = controller::setDebugTimeline,
            )

            AppScreen.NARRATOR -> NarratorScreen(
                uiState = uiState,
                playbackState = playbackState,
                onPlayPause = controller::playOrPause,
                onRestart = controller::restartNarration,
            )
        }
    }
}
