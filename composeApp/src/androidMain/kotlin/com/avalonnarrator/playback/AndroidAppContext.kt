package com.avalonnarrator.playback

import android.content.Context

object AndroidAppContext {
    @Volatile
    private var appContext: Context? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    fun requireContext(): Context =
        checkNotNull(appContext) {
            "AndroidAppContext is not initialized. Ensure MainActivity initializes it before playback."
        }
}
