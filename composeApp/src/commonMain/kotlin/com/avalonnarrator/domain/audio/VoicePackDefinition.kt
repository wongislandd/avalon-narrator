package com.avalonnarrator.domain.audio

data class VoicePackDefinition(
    val id: VoicePackId,
    val displayName: String,
    val clipFiles: Map<ClipId, String>,
)
