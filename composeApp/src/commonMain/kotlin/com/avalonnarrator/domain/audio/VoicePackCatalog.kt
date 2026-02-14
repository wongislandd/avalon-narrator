package com.avalonnarrator.domain.audio

object VoicePackCatalog {
    val defaultPackId: VoicePackId = VoicePackId.WIZARD

    private val wizard = VoicePackDefinition(
        id = VoicePackId.WIZARD,
        displayName = "Wizard",
        description = "Deep theatrical narration with a dark-fantasy tone for full-table drama.",
        clipFiles = ClipId.entries.associateWith { clip ->
            "audio/wizard/${clip.name.lowercase()}.mp3"
        },
    )

    private val rainbird = VoicePackDefinition(
        id = VoicePackId.RAINBIRD_EN,
        displayName = "Rainbird",
        description = "Calm British delivery with clear pacing and softer emphasis for readability.",
        clipFiles = ClipId.entries.associateWith { clip ->
            "audio/rainbird_en/${clip.name.lowercase()}.mp3"
        },
    )

    private val packs = mapOf(
        wizard.id to wizard,
        rainbird.id to rainbird,
    )

    fun all(): List<VoicePackDefinition> = packs.values.toList()

    fun byId(id: VoicePackId): VoicePackDefinition? = packs[id]
}
