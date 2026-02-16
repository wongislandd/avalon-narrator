package com.avalonnarrator.navigation

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    // Browser navigation is handled by explicit in-app actions.
}
