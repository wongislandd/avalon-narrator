package com.avalonnarrator.engine.planner

import com.avalonnarrator.domain.narration.NarrationPlan
import com.avalonnarrator.domain.setup.GameSetupConfig

interface NarrationPlanner {
    fun plan(config: GameSetupConfig): NarrationPlan
}
