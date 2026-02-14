package com.avalonnarrator.presentation.narrator

sealed interface NarratorUiEvent {
    data object PlayPause : NarratorUiEvent
    data object Restart : NarratorUiEvent
    data object NextStep : NarratorUiEvent
    data object Back : NarratorUiEvent
}
