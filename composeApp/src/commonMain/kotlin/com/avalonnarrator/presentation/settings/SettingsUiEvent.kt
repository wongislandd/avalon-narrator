package com.avalonnarrator.presentation.settings

import com.avalonnarrator.domain.audio.VoicePackId
import com.avalonnarrator.domain.setup.NarrationPace

sealed interface SettingsUiEvent {
    data class ToggleValidators(val enabled: Boolean) : SettingsUiEvent
    data class SetPace(val pace: NarrationPace) : SettingsUiEvent
    data object RegenerateSeed : SettingsUiEvent
    data class SetVoicePack(val voicePackId: VoicePackId) : SettingsUiEvent
    data object OpenVoiceSelection : SettingsUiEvent
    data object CloseVoiceSelection : SettingsUiEvent
    data class PreviewVoicePack(val voicePackId: VoicePackId) : SettingsUiEvent
    data class ToggleReminders(val enabled: Boolean) : SettingsUiEvent
    data class ToggleDebugTimeline(val enabled: Boolean) : SettingsUiEvent
    data object Back : SettingsUiEvent
}
