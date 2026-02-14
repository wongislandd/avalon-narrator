package com.avalonnarrator.app

import com.avalonnarrator.domain.audio.VoicePackId
import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.GameModule
import com.avalonnarrator.domain.setup.GameSetupConfig
import com.avalonnarrator.domain.setup.NarrationPace
import com.avalonnarrator.domain.setup.RosterBuilder
import com.avalonnarrator.domain.setup.SetupIssueLevel
import com.avalonnarrator.engine.planner.DefaultNarrationPlanner
import com.avalonnarrator.engine.planner.NarrationPlanner
import com.avalonnarrator.engine.validation.DefaultSetupValidator
import com.avalonnarrator.engine.validation.SetupValidator
import com.avalonnarrator.navigation.AppScreen
import com.avalonnarrator.playback.DefaultClipResolver
import com.avalonnarrator.playback.DefaultNarrationPlayer
import com.avalonnarrator.playback.NarrationPlayer
import com.avalonnarrator.settings.SettingsSetupStore
import com.avalonnarrator.settings.SetupStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class AvalonAppController(
    private val setupStore: SetupStore = SettingsSetupStore(),
    private val setupValidator: SetupValidator = DefaultSetupValidator(),
    private val narrationPlanner: NarrationPlanner = DefaultNarrationPlanner(),
    private val narrationPlayer: NarrationPlayer = DefaultNarrationPlayer(DefaultClipResolver()),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) {
    private val _uiState = MutableStateFlow(AvalonUiState())
    val uiState: StateFlow<AvalonUiState> = _uiState.asStateFlow()

    val playbackState = narrationPlayer.state

    fun initialize() {
        if (_uiState.value.isInitialized) return
        scope.launch {
            val restoredConfig = normalizeConfig(setupStore.loadLast() ?: GameSetupConfig())
            val issues = calculateIssues(restoredConfig)
            _uiState.value = _uiState.value.copy(
                config = restoredConfig,
                issues = issues,
                isInitialized = true,
            )
        }
    }

    fun navigate(screen: AppScreen) {
        _uiState.value = _uiState.value.copy(screen = screen)
    }

    fun toggleRole(roleId: com.avalonnarrator.domain.roles.RoleId) {
        if (roleId == RoleId.LOYAL_SERVANT || roleId == RoleId.MINION) return
        mutateConfig { config ->
            val nextRoles = config.selectedRoles.toMutableSet()
            if (roleId in nextRoles) {
                nextRoles.remove(roleId)
                return@mutateConfig config.copy(selectedRoles = nextRoles)
            }

            config.copy(selectedRoles = nextRoles + roleId)
        }
    }

    fun setPlayerCount(playerCount: Int) {
        mutateConfig { it.copy(playerCount = playerCount.coerceIn(5, 10)) }
    }

    fun increaseLoyalServants() {
        adjustLoyalServantsBy(1)
    }

    fun decreaseLoyalServants() {
        adjustLoyalServantsBy(-1)
    }

    fun increaseMinions() {
        adjustMinionsBy(1)
    }

    fun decreaseMinions() {
        adjustMinionsBy(-1)
    }

    fun setLoyalServants(count: Int) {
        setLoyalServantCount(count)
    }

    fun setMinions(count: Int) {
        setMinionCount(count)
    }

    fun toggleLoyalServantSelection() {
        val current = RosterBuilder.build(_uiState.value.config).loyalServantCount
        setLoyalServantCount(if (current > 0) 0 else 1)
    }

    fun toggleMinionSelection() {
        val current = RosterBuilder.build(_uiState.value.config).minionCount
        setMinionCount(if (current > 0) 0 else 1)
    }

    fun toggleModule(module: GameModule) {
        if (module == GameModule.LANCELOT) return
        mutateConfig { config ->
            val modules = config.enabledModules.toMutableSet().apply {
                if (module in this) remove(module) else add(module)
            }
            config.copy(enabledModules = modules)
        }
    }

    fun setNarrationPace(pace: NarrationPace) {
        mutateConfig { it.copy(narrationPace = pace) }
    }

    fun regenerateSeed() {
        mutateConfig { it.copy(randomSeed = Random.nextLong()) }
    }

    fun setVoicePack(voicePackId: VoicePackId) {
        mutateConfig { it.copy(selectedVoicePack = voicePackId) }
    }

    fun setDebugTimeline(enabled: Boolean) {
        mutateConfig { it.copy(debugTimelineEnabled = enabled) }
    }

    fun setValidatorsEnabled(enabled: Boolean) {
        mutateConfig { it.copy(validatorsEnabled = enabled) }
    }

    fun setNarrationReminders(enabled: Boolean) {
        mutateConfig { it.copy(narrationRemindersEnabled = enabled) }
    }

    fun startNarrationRun() {
        val config = _uiState.value.config
        val issues = calculateIssues(config)
        if (issues.any { it.level == SetupIssueLevel.ERROR || it.code in blockingIssueCodes }) {
            _uiState.value = _uiState.value.copy(issues = issues)
            return
        }
        val plan = narrationPlanner.plan(config)
        narrationPlayer.load(plan)
        _uiState.value = _uiState.value.copy(
            narrationPlan = plan,
            issues = issues,
            screen = AppScreen.NARRATOR,
        )
    }

    fun playOrPause() {
        if (playbackState.value.isPlaying) narrationPlayer.pause() else narrationPlayer.play()
    }

    fun restartNarration() {
        narrationPlayer.restart()
    }

    fun nextNarrationStep() {
        narrationPlayer.nextStep()
    }

    private fun mutateConfig(transform: (GameSetupConfig) -> GameSetupConfig) {
        val nextConfig = normalizeConfig(transform(_uiState.value.config))
        val issues = calculateIssues(nextConfig)
        _uiState.value = _uiState.value.copy(config = nextConfig, issues = issues)
        scope.launch { setupStore.save(nextConfig) }
    }

    private fun normalizeConfig(config: GameSetupConfig): GameSetupConfig {
        val selectedSpecial = config.selectedRoles
            .filter { it in RosterBuilder.selectableRoleIds() }
            .toSet()
        val loyalServantCount = config.loyalServantAdjustment.coerceIn(0, 12)
        val minionCount = config.minionAdjustment.coerceIn(0, 12)
        val derivedPlayerCount = selectedSpecial.size + loyalServantCount + minionCount
        val inferredModules = buildSet {
            addAll(config.enabledModules - GameModule.LANCELOT)
            if (RoleId.LANCELOT_GOOD in selectedSpecial || RoleId.LANCELOT_EVIL in selectedSpecial) {
                add(GameModule.LANCELOT)
            }
        }

        return config.copy(
            playerCount = derivedPlayerCount,
            selectedRoles = selectedSpecial,
            loyalServantAdjustment = loyalServantCount,
            minionAdjustment = minionCount,
            enabledModules = inferredModules,
        )
    }

    private fun calculateIssues(config: GameSetupConfig): List<com.avalonnarrator.domain.setup.SetupIssue> =
        if (config.validatorsEnabled) setupValidator.validate(config) else emptyList()

    private fun adjustLoyalServantsBy(delta: Int) {
        val current = RosterBuilder.build(_uiState.value.config).loyalServantCount
        setLoyalServantCount(current + delta)
    }

    private fun adjustMinionsBy(delta: Int) {
        val current = RosterBuilder.build(_uiState.value.config).minionCount
        setMinionCount(current + delta)
    }

    private fun setLoyalServantCount(count: Int) {
        mutateConfig { config ->
            val roster = RosterBuilder.build(config)
            val nextCount = count.coerceIn(0, 12)
            config.copy(loyalServantAdjustment = nextCount - roster.autoLoyalServantCount)
        }
    }

    private fun setMinionCount(count: Int) {
        mutateConfig { config ->
            val roster = RosterBuilder.build(config)
            val nextCount = count.coerceIn(0, 12)
            config.copy(minionAdjustment = nextCount - roster.autoMinionCount)
        }
    }

    companion object {
        private val blockingIssueCodes = setOf(
            "MIN_PLAYERS_NOT_MET",
            "MAX_PLAYERS_EXCEEDED",
            "LANCELOT_PAIR_REQUIRED",
        )
    }
}
