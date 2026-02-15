package com.avalonnarrator.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class NavigationDirection {
    FORWARD,
    BACKWARD,
    NONE,
}

data class NavigationState(
    val screen: AppScreen,
    val direction: NavigationDirection,
)

class AppNavigatorState(initial: AppScreen = AppScreen.SETUP) {
    private val backStack = mutableListOf(initial)

    var navigationState by mutableStateOf(
        NavigationState(
            screen = initial,
            direction = NavigationDirection.NONE,
        ),
    )
        private set

    val currentScreen: AppScreen
        get() = navigationState.screen

    fun navigate(screen: AppScreen) {
        val current = currentScreen
        if (screen == current) {
            return
        }

        val previous = backStack.getOrNull(backStack.lastIndex - 1)
        val direction = if (screen == previous) {
            backStack.removeAt(backStack.lastIndex)
            NavigationDirection.BACKWARD
        } else {
            backStack.add(screen)
            NavigationDirection.FORWARD
        }

        navigationState = NavigationState(
            screen = screen,
            direction = direction,
        )
    }
}
