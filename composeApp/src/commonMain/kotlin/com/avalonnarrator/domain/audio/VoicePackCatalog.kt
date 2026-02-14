package com.avalonnarrator.domain.audio

object VoicePackCatalog {
    val defaultPackId: VoicePackId = VoicePackId.DRAMATIC_EN

    private val dramatic = VoicePackDefinition(
        id = VoicePackId.DRAMATIC_EN,
        displayName = "Dramatic English",
        clipFiles = ClipId.entries.associateWith { clip ->
            "audio/dramatic_en/${clip.name.lowercase()}.mp3"
        },
    )

    private val rainbird = VoicePackDefinition(
        id = VoicePackId.RAINBIRD_EN,
        displayName = "Rainbird",
        clipFiles = ClipId.entries.associateWith { clip ->
            "audio/rainbird_en/${clip.name.lowercase()}.mp3"
        },
    )

    private val packs = mapOf(
        dramatic.id to dramatic,
        rainbird.id to rainbird,
    )

    fun all(): List<VoicePackDefinition> = packs.values.toList()

    fun byId(id: VoicePackId): VoicePackDefinition? = packs[id]
}
