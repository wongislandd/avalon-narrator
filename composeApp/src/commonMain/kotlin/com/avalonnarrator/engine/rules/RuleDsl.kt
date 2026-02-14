package com.avalonnarrator.engine.rules

import com.avalonnarrator.domain.audio.ClipId
import com.avalonnarrator.domain.setup.GameModule
import com.avalonnarrator.domain.roles.RoleId

class RuleStepBuilder internal constructor(
    private val id: String,
    private val phase: RulePhase,
    private val order: Int,
) {
    private val requiresAllRoles: MutableSet<RoleId> = linkedSetOf()
    private val requiresAnyRoles: MutableSet<RoleId> = linkedSetOf()
    private val excludesRoles: MutableSet<RoleId> = linkedSetOf()
    private val requiredModules: MutableSet<GameModule> = linkedSetOf()
    private val clips: MutableList<ClipId> = mutableListOf()
    private var requiresNarrationRemindersEnabled: Boolean? = null
    private var minPlayers: Int? = null
    private var maxPlayers: Int? = null
    private var interClipDelayMs: Long = 900L
    private var baseDelayMs: Long = 1200L

    fun requires(role: RoleId) {
        requiresAllRoles += role
    }

    fun requiresAny(vararg roles: RoleId) {
        requiresAnyRoles += roles
    }

    fun excludes(role: RoleId) {
        excludesRoles += role
    }

    fun requiresModule(module: GameModule) {
        requiredModules += module
    }

    fun requiresNarrationRemindersEnabled(enabled: Boolean = true) {
        requiresNarrationRemindersEnabled = enabled
    }

    fun playersBetween(min: Int? = null, max: Int? = null) {
        minPlayers = min
        maxPlayers = max
    }

    fun clips(vararg clips: ClipId) {
        this.clips += clips
    }

    fun interClipDelayMs(delay: Long) {
        interClipDelayMs = delay
    }

    fun baseDelayMs(delay: Long) {
        baseDelayMs = delay
    }

    internal fun build(): RuleStepDefinition = RuleStepDefinition(
        id = id,
        phase = phase,
        order = order,
        condition = RuleCondition(
            requiresAllRoles = requiresAllRoles,
            requiresAnyRoles = requiresAnyRoles,
            excludesRoles = excludesRoles,
            requiredModules = requiredModules,
            requiresNarrationRemindersEnabled = requiresNarrationRemindersEnabled,
            minPlayers = minPlayers,
            maxPlayers = maxPlayers,
        ),
        clips = clips.toList(),
        interClipDelayMs = interClipDelayMs,
        baseDelayMs = baseDelayMs,
    )
}

fun ruleStep(
    id: String,
    phase: RulePhase,
    order: Int,
    block: RuleStepBuilder.() -> Unit,
): RuleStepDefinition = RuleStepBuilder(id, phase, order).apply(block).build()
