package com.avalonnarrator.presentation.settings

import com.avalonnarrator.domain.audio.VoicePackId

sealed interface SettingsUiEvent {
    data class ToggleValidators(val enabled: Boolean) : SettingsUiEvent
    data object IncreaseRegularPause : SettingsUiEvent
    data object DecreaseRegularPause : SettingsUiEvent
    data object IncreaseActionPause : SettingsUiEvent
    data object DecreaseActionPause : SettingsUiEvent
    data class SetVoicePack(val voicePackId: VoicePackId) : SettingsUiEvent
    data object OpenVoiceSelection : SettingsUiEvent
    data object CloseVoiceSelection : SettingsUiEvent
    data class PreviewVoicePack(val voicePackId: VoicePackId) : SettingsUiEvent
    data class ToggleReminders(val enabled: Boolean) : SettingsUiEvent
    data class ToggleDebugTimeline(val enabled: Boolean) : SettingsUiEvent
    data object Back : SettingsUiEvent
}
