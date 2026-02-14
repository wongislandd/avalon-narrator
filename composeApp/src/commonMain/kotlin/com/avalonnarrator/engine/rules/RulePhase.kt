package com.avalonnarrator.engine.rules

enum class RulePhase(val sortOrder: Int) {
    PRELUDE(0),
    EVIL_INFO(1),
    GOOD_INFO(2),
    MODULES(3),
    CLOSING(4),
}
