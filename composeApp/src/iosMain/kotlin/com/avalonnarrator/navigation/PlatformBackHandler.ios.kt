package com.avalonnarrator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.interop.LocalUIViewController
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIRectEdgeLeft
import platform.UIKit.UIScreenEdgePanGestureRecognizer
import platform.UIKit.UIGestureRecognizerStateEnded
import platform.darwin.NSObject

private const val EDGE_BACK_SWIPE_ACTION = "handleBackSwipe:"

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    if (!enabled) {
        return
    }

    val viewController = LocalUIViewController.current
    val latestOnBack by rememberUpdatedState(onBack)

    DisposableEffect(viewController) {
        val target = EdgeBackSwipeTarget {
            latestOnBack()
        }
        val recognizer = UIScreenEdgePanGestureRecognizer(
            target = target,
            action = NSSelectorFromString(EDGE_BACK_SWIPE_ACTION),
        ).apply {
            edges = UIRectEdgeLeft
            cancelsTouchesInView = false
        }

        viewController.view.addGestureRecognizer(recognizer)

        onDispose {
            viewController.view.removeGestureRecognizer(recognizer)
        }
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class EdgeBackSwipeTarget(
    private val onBack: () -> Unit,
) : NSObject() {

    @ObjCAction
    fun handleBackSwipe(recognizer: UIScreenEdgePanGestureRecognizer) {
        if (recognizer.state == UIGestureRecognizerStateEnded) {
            onBack()
        }
    }
}
