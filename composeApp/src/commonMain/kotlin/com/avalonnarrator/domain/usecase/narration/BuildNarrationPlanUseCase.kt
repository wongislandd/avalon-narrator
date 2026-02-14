package com.avalonnarrator.domain.usecase.narration

import com.avalonnarrator.domain.narration.NarrationPlan
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.engine.planner.NarrationPlanner

class BuildNarrationPlanUseCase(
    private val narrationPlanner: NarrationPlanner,
) {
    operator fun invoke(config: GameSetupConfig): NarrationPlan = narrationPlanner.plan(config)
}
