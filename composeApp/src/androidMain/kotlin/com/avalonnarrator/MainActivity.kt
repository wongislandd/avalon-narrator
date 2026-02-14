package com.avalonnarrator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.avalonnarrator.app.App
import com.avalonnarrator.playback.AndroidAppContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidAppContext.initialize(applicationContext)
        setContent {
            App()
        }
    }
}
