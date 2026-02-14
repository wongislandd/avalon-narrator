package com.avalonnarrator.playback

import com.avalonnarrator.domain.audio.ClipId
import com.avalonnarrator.domain.audio.VoicePackCatalog
import com.avalonnarrator.domain.audio.VoicePackId

class DefaultClipResolver : ClipResolver {
    override fun resolve(clipId: ClipId, voicePack: VoicePackId): ClipResolution {
        val selectedPack = VoicePackCatalog.byId(voicePack)
        val defaultPack = VoicePackCatalog.byId(VoicePackCatalog.defaultPackId)

        val primary = selectedPack?.clipFiles?.get(clipId)
        if (primary != null) {
            return ClipResolution.Found(
                ResolvedClip(
                    clipId = clipId,
                    assetPath = primary,
                    sourceVoicePack = voicePack,
                    usedFallback = false,
                ),
            )
        }

        val fallback = defaultPack?.clipFiles?.get(clipId)
        if (fallback != null) {
            return ClipResolution.Found(
                ResolvedClip(
                    clipId = clipId,
                    assetPath = fallback,
                    sourceVoicePack = VoicePackCatalog.defaultPackId,
                    usedFallback = true,
                ),
            )
        }

        return ClipResolution.Missing(
            clipId = clipId,
            attemptedVoicePacks = listOfNotNull(voicePack, VoicePackCatalog.defaultPackId).distinct(),
        )
    }
}
