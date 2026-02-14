package com.avalonnarrator.settings

import com.avalonnarrator.domain.audio.VoicePackCatalog
import com.avalonnarrator.domain.audio.VoicePackId
import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.GameModule
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.russhwolf.settings.Settings

class SettingsSetupStore(
    private val settings: Settings = Settings(),
) : SetupStore {

    override suspend fun loadLast(): GameSetupConfig? {
        if (!settings.hasKey(KEY_PLAYER_COUNT)) return null
        val defaults = GameSetupConfig()

        return GameSetupConfig(
            playerCount = settings.getInt(KEY_PLAYER_COUNT, defaults.playerCount),
            selectedRoles = parseRoles(settings.getString(KEY_SELECTED_ROLES, "")),
            loyalServantAdjustment = settings.getInt(KEY_LOYAL_SERVANT_ADJUSTMENT, defaults.loyalServantAdjustment),
            minionAdjustment = settings.getInt(KEY_MINION_ADJUSTMENT, defaults.minionAdjustment),
            validatorsEnabled = settings.getBoolean(KEY_VALIDATORS_ENABLED, defaults.validatorsEnabled),
            enabledModules = parseModules(settings.getString(KEY_ENABLED_MODULES, "")),
            narrationPace = enumValueOrDefault(settings.getString(KEY_PACE, defaults.narrationPace.name), defaults.narrationPace),
            randomSeed = settings.getLongOrNull(KEY_RANDOM_SEED),
            selectedVoicePack = parseVoicePack(
                settings.getString(KEY_VOICE_PACK, defaults.selectedVoicePack),
                defaults.selectedVoicePack,
            ),
            narrationRemindersEnabled = settings.getBoolean(KEY_NARRATION_REMINDERS, defaults.narrationRemindersEnabled),
            debugTimelineEnabled = settings.getBoolean(KEY_DEBUG_TIMELINE, defaults.debugTimelineEnabled),
        )
    }

    override suspend fun save(config: GameSetupConfig) {
        settings.putInt(KEY_PLAYER_COUNT, config.playerCount)
        settings.putString(KEY_SELECTED_ROLES, config.selectedRoles.joinToString(SEPARATOR) { it.name })
        settings.putInt(KEY_LOYAL_SERVANT_ADJUSTMENT, config.loyalServantAdjustment)
        settings.putInt(KEY_MINION_ADJUSTMENT, config.minionAdjustment)
        settings.putBoolean(KEY_VALIDATORS_ENABLED, config.validatorsEnabled)
        settings.putString(KEY_ENABLED_MODULES, config.enabledModules.joinToString(SEPARATOR) { it.name })
        settings.putString(KEY_PACE, config.narrationPace.name)
        settings.putString(KEY_VOICE_PACK, config.selectedVoicePack)
        settings.putBoolean(KEY_NARRATION_REMINDERS, config.narrationRemindersEnabled)
        settings.putBoolean(KEY_DEBUG_TIMELINE, config.debugTimelineEnabled)
        config.randomSeed?.let { settings.putLong(KEY_RANDOM_SEED, it) } ?: settings.remove(KEY_RANDOM_SEED)
    }

    private fun parseRoles(raw: String): Set<RoleId> = raw
        .split(SEPARATOR)
        .mapNotNull { token -> token.takeIf { it.isNotBlank() }?.let { runCatching { enumValueOf<RoleId>(it) }.getOrNull() } }
        .toSet()
        .ifEmpty { GameSetupConfig().selectedRoles }

    private fun parseModules(raw: String): Set<GameModule> = raw
        .split(SEPARATOR)
        .mapNotNull { token -> token.takeIf { it.isNotBlank() }?.let { runCatching { enumValueOf<GameModule>(it) }.getOrNull() } }
        .toSet()

    private inline fun <reified T : Enum<T>> enumValueOrDefault(raw: String, default: T): T =
        runCatching { enumValueOf<T>(raw) }.getOrElse { default }

    private fun parseVoicePack(raw: String, default: VoicePackId): VoicePackId =
        raw.takeIf { VoicePackCatalog.byId(it) != null } ?: default

    companion object {
        private const val KEY_PLAYER_COUNT = "player_count"
        private const val KEY_SELECTED_ROLES = "selected_roles"
        private const val KEY_LOYAL_SERVANT_ADJUSTMENT = "loyal_servant_adjustment"
        private const val KEY_MINION_ADJUSTMENT = "minion_adjustment"
        private const val KEY_VALIDATORS_ENABLED = "validators_enabled"
        private const val KEY_ENABLED_MODULES = "enabled_modules"
        private const val KEY_PACE = "narration_pace"
        private const val KEY_RANDOM_SEED = "random_seed"
        private const val KEY_VOICE_PACK = "voice_pack"
        private const val KEY_NARRATION_REMINDERS = "narration_reminders"
        private const val KEY_DEBUG_TIMELINE = "debug_timeline"
        private const val SEPARATOR = ","
    }
}
