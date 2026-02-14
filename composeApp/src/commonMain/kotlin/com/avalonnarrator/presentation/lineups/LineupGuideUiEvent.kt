package com.avalonnarrator.presentation.lineups

import com.avalonnarrator.domain.recommendation.RecommendedLineupDefinition

sealed interface LineupGuideUiEvent {
    data object IncreasePlayers : LineupGuideUiEvent
    data object DecreasePlayers : LineupGuideUiEvent
    data class ApplyLineup(val lineup: RecommendedLineupDefinition) : LineupGuideUiEvent
    data object Back : LineupGuideUiEvent
}
