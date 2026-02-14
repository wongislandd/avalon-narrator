package com.avalonnarrator.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AppNavigatorState(initial: AppScreen = AppScreen.SETUP) {
    var currentScreen by mutableStateOf(initial)
        private set

    fun navigate(screen: AppScreen) {
        currentScreen = screen
    }
}
