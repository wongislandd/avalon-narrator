package com.avalonnarrator.domain.setup

enum class NarrationPace(val delayMultiplier: Double) {
    SLOW(1.25),
    NORMAL(1.0),
    FAST(0.75),
}
