package com.avalonnarrator.presentation.settings

import com.avalonnarrator.navigation.AppScreen

sealed interface SettingsUiEffect {
    data class Navigate(val screen: AppScreen) : SettingsUiEffect
}
