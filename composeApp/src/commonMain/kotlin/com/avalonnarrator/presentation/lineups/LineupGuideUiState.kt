package com.avalonnarrator.presentation.lineups

import com.avalonnarrator.domain.recommendation.RecommendedLineupCatalog
import com.avalonnarrator.domain.recommendation.RecommendedLineupDefinition

data class LineupGuideUiState(
    val isInitialized: Boolean = false,
    val selectedPlayerCount: Int = 5,
    val lineups: List<RecommendedLineupDefinition> = RecommendedLineupCatalog.forPlayerCount(5),
)
