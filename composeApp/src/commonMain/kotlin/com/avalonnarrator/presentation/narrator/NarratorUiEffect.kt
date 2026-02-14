package com.avalonnarrator.presentation.narrator

import com.avalonnarrator.navigation.AppScreen

sealed interface NarratorUiEffect {
    data class Navigate(val screen: AppScreen) : NarratorUiEffect
}
