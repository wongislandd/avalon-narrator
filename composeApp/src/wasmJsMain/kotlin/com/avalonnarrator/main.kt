package com.avalonnarrator

import androidx.compose.ui.window.CanvasBasedWindow
import androidx.compose.ui.ExperimentalComposeUiApi
import com.avalonnarrator.app.App

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow("Avalon Narrator") {
        App()
    }
}
