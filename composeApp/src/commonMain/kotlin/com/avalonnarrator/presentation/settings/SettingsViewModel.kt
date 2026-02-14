package com.avalonnarrator.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avalonnarrator.app.di.SetupSession
import com.avalonnarrator.domain.audio.NarrationScriptCatalog
import com.avalonnarrator.domain.audio.VoicePackCatalog
import com.avalonnarrator.domain.audio.VoicePackId
import com.avalonnarrator.domain.usecase.setup.SetupMutation
import com.avalonnarrator.navigation.AppScreen
import com.avalonnarrator.playback.ClipResolution
import com.avalonnarrator.playback.DefaultClipResolver
import com.avalonnarrator.playback.createPlatformAudioEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val setupSession: SetupSession,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<SettingsUiEffect>()
    val effects = _effects.asSharedFlow()
    private val clipResolver = DefaultClipResolver()
    private val previewAudioEngine = createPlatformAudioEngine()
    private var previewJob: Job? = null

    init {
        observeSetup()
    }

    fun onEvent(event: SettingsUiEvent) {
        when (event) {
            is SettingsUiEvent.ToggleValidators -> setupSession.mutate(SetupMutation.SetValidatorsEnabled(event.enabled))
            is SettingsUiEvent.SetPace -> setupSession.mutate(SetupMutation.SetNarrationPace(event.pace))
            SettingsUiEvent.RegenerateSeed -> setupSession.mutate(SetupMutation.RegenerateSeed)
            is SettingsUiEvent.SetVoicePack -> setupSession.mutate(SetupMutation.SetVoicePack(event.voicePackId))
            SettingsUiEvent.OpenVoiceSelection -> {
                viewModelScope.launch {
                    _effects.emit(SettingsUiEffect.Navigate(AppScreen.VOICE_SELECTION))
                }
            }

            SettingsUiEvent.CloseVoiceSelection -> {
                stopPreview()
                viewModelScope.launch {
                    _effects.emit(SettingsUiEffect.Navigate(AppScreen.SETTINGS))
                }
            }

            is SettingsUiEvent.PreviewVoicePack -> previewVoicePack(event.voicePackId)
            is SettingsUiEvent.ToggleReminders -> setupSession.mutate(
                SetupMutation.SetNarrationRemindersEnabled(event.enabled),
            )

            is SettingsUiEvent.ToggleDebugTimeline -> setupSession.mutate(
                SetupMutation.SetDebugTimelineEnabled(event.enabled),
            )

            SettingsUiEvent.Back -> {
                stopPreview()
                viewModelScope.launch {
                    _effects.emit(SettingsUiEffect.Navigate(AppScreen.SETUP))
                }
            }
        }
    }

    private fun observeSetup() {
        viewModelScope.launch {
            combine(
                setupSession.config,
                setupSession.isInitialized,
            ) { config, initialized -> config to initialized }.collectLatest { (config, initialized) ->
                _uiState.update {
                    it.copy(
                        config = config,
                        isInitialized = initialized,
                    )
                }
            }
        }
    }

    private fun previewVoicePack(voicePackId: VoicePackId) {
        val pack = VoicePackCatalog.byId(voicePackId) ?: return
        val clipCandidates = pack.clipFiles.keys.toList()
        if (clipCandidates.isEmpty()) {
            _uiState.update {
                it.copy(
                    previewingVoicePackId = null,
                    voicePreviewStatus = "No preview clips available for ${pack.displayName}.",
                )
            }
            return
        }

        stopPreview()

        val clipId = clipCandidates.random()
        val line = NarrationScriptCatalog.lineFor(clipId)
        _uiState.update {
            it.copy(
                previewingVoicePackId = voicePackId,
                voicePreviewStatus = "Previewing: \"$line\"",
            )
        }

        previewJob = viewModelScope.launch {
            when (val resolution = clipResolver.resolve(clipId, voicePackId)) {
                is ClipResolution.Found -> {
                    previewAudioEngine.play(resolution.clip.assetPath)
                    val fallbackLabel = if (resolution.clip.usedFallback) " (fallback)" else ""
                    _uiState.update {
                        it.copy(
                            previewingVoicePackId = null,
                            voicePreviewStatus = "Last preview ${pack.displayName}$fallbackLabel: \"$line\"",
                        )
                    }
                }

                is ClipResolution.Missing -> {
                    _uiState.update {
                        it.copy(
                            previewingVoicePackId = null,
                            voicePreviewStatus = "Missing preview clip ${resolution.clipId}.",
                        )
                    }
                }
            }
        }
    }

    private fun stopPreview() {
        previewJob?.cancel()
        previewJob = null
        previewAudioEngine.stop()
        _uiState.update { it.copy(previewingVoicePackId = null) }
    }

    override fun onCleared() {
        stopPreview()
        super.onCleared()
    }
}
