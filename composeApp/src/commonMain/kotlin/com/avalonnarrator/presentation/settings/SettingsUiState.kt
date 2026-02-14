package com.avalonnarrator.presentation.settings

import com.avalonnarrator.domain.audio.VoicePackCatalog
import com.avalonnarrator.domain.audio.VoicePackDefinition
import com.avalonnarrator.domain.audio.VoicePackId
import com.avalonnarrator.domain.setup.GameSetupConfig

data class SettingsUiState(
    val isInitialized: Boolean = false,
    val config: GameSetupConfig = GameSetupConfig(),
    val availableVoicePacks: List<VoicePackDefinition> = VoicePackCatalog.all(),
    val previewingVoicePackId: VoicePackId? = null,
    val voicePreviewStatus: String? = null,
)
