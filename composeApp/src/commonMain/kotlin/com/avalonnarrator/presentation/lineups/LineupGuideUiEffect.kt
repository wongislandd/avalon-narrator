package com.avalonnarrator.presentation.lineups

import com.avalonnarrator.navigation.AppScreen

sealed interface LineupGuideUiEffect {
    data class Navigate(val screen: AppScreen) : LineupGuideUiEffect
}
