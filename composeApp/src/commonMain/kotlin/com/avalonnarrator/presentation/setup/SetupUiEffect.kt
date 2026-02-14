package com.avalonnarrator.presentation.setup

import com.avalonnarrator.navigation.AppScreen

sealed interface SetupUiEffect {
    data class Navigate(val screen: AppScreen) : SetupUiEffect
}
