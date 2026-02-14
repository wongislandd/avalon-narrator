package com.avalonnarrator.playback

import com.avalonnarrator.domain.narration.NarrationPlan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DefaultNarrationPlayer(
    private val clipResolver: ClipResolver,
    private val audioEngine: PlatformAudioEngine = createPlatformAudioEngine(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) : NarrationPlayer {

    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private var plan: NarrationPlan? = null
    private var playbackJob: Job? = null

    override fun load(plan: NarrationPlan) {
        playbackJob?.cancel()
        audioEngine.stop()
        this.plan = plan
        _state.value = PlaybackState(
            isPlaying = false,
            currentStepIndex = if (plan.steps.isEmpty()) -1 else 0,
            currentClipIndex = -1,
            isInDelay = false,
            debugMessages = emptyList(),
        )
    }

    override fun play() {
        if (_state.value.isPlaying) return
        val currentPlan = plan ?: return
        if (currentPlan.steps.isEmpty()) return

        playbackJob?.cancel()
        playbackJob = scope.launch {
            audioEngine.startBacktrack(DEFAULT_BACKTRACK_ASSET_PATH, DEFAULT_BACKTRACK_VOLUME)
            _state.value = _state.value.copy(isPlaying = true)
            val startStep = _state.value.currentStepIndex.coerceAtLeast(0)
            try {
                for (stepIndex in startStep until currentPlan.steps.size) {
                    val step = currentPlan.steps[stepIndex]
                    _state.value = _state.value.copy(
                        currentStepIndex = stepIndex,
                        currentClipIndex = -1,
                        isInDelay = false,
                    )

                    for ((clipIndex, clip) in step.clips.withIndex()) {
                        _state.value = _state.value.copy(currentClipIndex = clipIndex, isInDelay = false)
                        when (val resolution = clipResolver.resolve(clip.clipId, currentPlan.voicePackId)) {
                            is ClipResolution.Found -> {
                                audioEngine.play(resolution.clip.assetPath)
                                if (resolution.clip.usedFallback) {
                                    appendDebug("Fallback clip for ${clip.clipId} from ${resolution.clip.sourceVoicePack}")
                                }
                            }

                            is ClipResolution.Missing -> {
                                appendDebug("Missing clip ${resolution.clipId} in packs ${resolution.attemptedVoicePacks}")
                            }
                        }
                    }

                    _state.value = _state.value.copy(currentClipIndex = -1, isInDelay = true)
                    delay(step.delayAfterMs)
                }
            } finally {
                audioEngine.stopBacktrack()
                _state.value = _state.value.copy(isPlaying = false, isInDelay = false)
            }
        }
    }

    override fun pause() {
        playbackJob?.cancel()
        audioEngine.pause()
        audioEngine.stopBacktrack()
        _state.value = _state.value.copy(isPlaying = false, isInDelay = false)
    }

    override fun restart() {
        pause()
        val loaded = plan ?: return
        _state.value = _state.value.copy(
            currentStepIndex = if (loaded.steps.isEmpty()) -1 else 0,
            currentClipIndex = -1,
            isInDelay = false,
            debugMessages = emptyList(),
        )
    }

    override fun nextStep() {
        val loaded = plan ?: return
        if (loaded.steps.isEmpty()) return
        val next = (_state.value.currentStepIndex + 1).coerceAtMost(loaded.steps.lastIndex)
        _state.value = _state.value.copy(currentStepIndex = next, currentClipIndex = -1, isInDelay = false)
    }

    private fun appendDebug(message: String) {
        _state.value = _state.value.copy(debugMessages = _state.value.debugMessages + message)
    }

    companion object {
        private const val DEFAULT_BACKTRACK_ASSET_PATH = "audio/backtrack/the_uncertain_quest.mp3"
        private const val DEFAULT_BACKTRACK_VOLUME = 0.22f
    }
}
