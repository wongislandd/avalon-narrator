package com.avalonnarrator.playback

import com.avalonnarrator.domain.audio.ClipId
import com.avalonnarrator.domain.audio.VoicePackId

data class ResolvedClip(
    val clipId: ClipId,
    val assetPath: String,
    val sourceVoicePack: VoicePackId,
    val usedFallback: Boolean,
)

sealed class ClipResolution {
    data class Found(val clip: ResolvedClip) : ClipResolution()
    data class Missing(val clipId: ClipId, val attemptedVoicePacks: List<VoicePackId>) : ClipResolution()
}

interface ClipResolver {
    fun resolve(clipId: ClipId, voicePack: VoicePackId): ClipResolution
}
