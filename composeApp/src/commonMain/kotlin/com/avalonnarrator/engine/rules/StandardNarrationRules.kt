package com.avalonnarrator.engine.rules

import com.avalonnarrator.domain.audio.ClipId
import com.avalonnarrator.domain.roles.RoleId
import com.avalonnarrator.domain.setup.GameModule

object StandardNarrationRules {
    val steps: List<RuleStepDefinition> = listOf(
        ruleStep(id = "intro", phase = RulePhase.PRELUDE, order = 0) {
            clips(ClipId.INTRO, ClipId.CLOSE_EYES, ClipId.HANDS_IN_FISTS)
            baseDelayMs(1400)
        },
        ruleStep(id = "cleric_alignment_check", phase = RulePhase.PRELUDE, order = 5) {
            requires(RoleId.CLERIC)
            clips(
                ClipId.CLERIC_LEADER_EVIL_THUMB,
                ClipId.CLERIC_OPEN_EYES,
                ClipId.CLERIC_CLOSE_EYES,
                ClipId.LEADER_RETURN_FIST,
            )
            baseDelayMs(1400)
        },
        ruleStep(id = "evil_info", phase = RulePhase.EVIL_INFO, order = 10) {
            requires(RoleId.ROGUE_EVIL)
            requiresAny(
                RoleId.ASSASSIN,
                RoleId.MORGANA,
                RoleId.MORDRED,
                RoleId.MINION,
                RoleId.LANCELOT_EVIL,
                RoleId.LUNATIC,
                RoleId.BRUTE,
                RoleId.TRICKSTER,
                RoleId.REVEALER,
                RoleId.EVIL_MESSENGER,
                RoleId.SORCERER_EVIL,
            )
            clips(ClipId.EVIL_WAKE_EXCEPT_EVIL_ROGUE, ClipId.EVIL_CLOSE)
            baseDelayMs(1500)
        },
        ruleStep(id = "evil_info", phase = RulePhase.EVIL_INFO, order = 11) {
            excludes(RoleId.ROGUE_EVIL)
            requiresAny(
                RoleId.ASSASSIN,
                RoleId.MORGANA,
                RoleId.MORDRED,
                RoleId.MINION,
                RoleId.LANCELOT_EVIL,
                RoleId.LUNATIC,
                RoleId.BRUTE,
                RoleId.TRICKSTER,
                RoleId.REVEALER,
                RoleId.EVIL_MESSENGER,
                RoleId.ROGUE_EVIL,
                RoleId.SORCERER_EVIL,
            )
            clips(ClipId.EVIL_WAKE, ClipId.EVIL_CLOSE)
            baseDelayMs(1500)
        },
        ruleStep(id = "merlin_info", phase = RulePhase.GOOD_INFO, order = 20) {
            requires(RoleId.MERLIN)
            requires(RoleId.SORCERER_EVIL)
            clips(
                ClipId.ALL_KEEP_EYES_CLOSED_FISTS,
                ClipId.MINIONS_EXTEND_THUMB_FOR_MERLIN_EXCEPT_EVIL_SORCERER,
                ClipId.MERLIN_WAKE,
                ClipId.MINIONS_RETURN_FIST_EXCEPT_EVIL_SORCERER,
                ClipId.MERLIN_CLOSE,
                ClipId.ALL_KEEP_EYES_CLOSED_FISTS,
            )
            baseDelayMs(1600)
        },
        ruleStep(id = "merlin_info", phase = RulePhase.GOOD_INFO, order = 21) {
            requires(RoleId.MERLIN)
            excludes(RoleId.SORCERER_EVIL)
            clips(
                ClipId.ALL_KEEP_EYES_CLOSED_FISTS,
                ClipId.MINIONS_EXTEND_THUMB_FOR_MERLIN,
                ClipId.MERLIN_WAKE,
                ClipId.MINIONS_RETURN_FIST,
                ClipId.MERLIN_CLOSE,
                ClipId.ALL_KEEP_EYES_CLOSED_FISTS,
            )
            baseDelayMs(1600)
        },
        ruleStep(id = "percival_info_pair", phase = RulePhase.GOOD_INFO, order = 30) {
            requires(RoleId.PERCIVAL)
            requires(RoleId.MORGANA)
            clips(
                ClipId.MERLIN_AND_MORGANA_EXTEND_THUMB,
                ClipId.PERCIVAL_WAKE,
                ClipId.MERLIN_AND_MORGANA_RETURN_FIST,
                ClipId.PERCIVAL_CLOSE,
            )
            baseDelayMs(1600)
        },
        ruleStep(id = "percival_info_merlin_only", phase = RulePhase.GOOD_INFO, order = 31) {
            requires(RoleId.PERCIVAL)
            requires(RoleId.MERLIN)
            excludes(RoleId.MORGANA)
            clips(
                ClipId.MERLIN_EXTEND_THUMB,
                ClipId.PERCIVAL_WAKE,
                ClipId.MERLIN_RETURN_FIST,
                ClipId.PERCIVAL_CLOSE,
            )
            baseDelayMs(1600)
        },
        ruleStep(id = "lancelot_counterpart", phase = RulePhase.MODULES, order = 40) {
            requiresModule(GameModule.LANCELOT)
            requires(RoleId.LANCELOT_GOOD)
            requires(RoleId.LANCELOT_EVIL)
            clips(ClipId.LANCELOT_WAKE, ClipId.LANCELOT_CLOSE)
            baseDelayMs(1700)
        },
        ruleStep(id = "messenger_pair_info", phase = RulePhase.MODULES, order = 44) {
            requires(RoleId.SENIOR_MESSENGER)
            requires(RoleId.JUNIOR_MESSENGER)
            clips(
                ClipId.JUNIOR_MESSENGER_EXTEND_THUMB,
                ClipId.SENIOR_MESSENGER_OPEN_EYES,
                ClipId.SENIOR_MESSENGER_CLOSE_EYES,
                ClipId.JUNIOR_MESSENGER_RETURN_FIST,
            )
            baseDelayMs(1300)
        },
        ruleStep(id = "untrustworthy_servant_info", phase = RulePhase.MODULES, order = 45) {
            requires(RoleId.UNTRUSTWORTHY_SERVANT)
            requires(RoleId.ASSASSIN)
            clips(
                ClipId.UNTRUSTWORTHY_AND_MINIONS_EXTEND_THUMB,
                ClipId.UNTRUSTWORTHY_AND_MINIONS_RETURN_FIST,
                ClipId.ASSASSIN_EXTEND_THUMB_FOR_UNTRUSTWORTHY,
                ClipId.UNTRUSTWORTHY_OPEN_EYES,
                ClipId.UNTRUSTWORTHY_CLOSE_EYES,
                ClipId.ASSASSIN_RETURN_FIST,
            )
            baseDelayMs(1300)
        },
        ruleStep(id = "lady_module", phase = RulePhase.MODULES, order = 50) {
            requiresModule(GameModule.LADY_OF_LAKE)
            clips(ClipId.LADY_OF_LAKE)
            baseDelayMs(1300)
        },
        ruleStep(id = "lady_module_pass", phase = RulePhase.MODULES, order = 51) {
            requiresModule(GameModule.LADY_OF_LAKE)
            clips(ClipId.LADY_PASS_TOKEN)
            baseDelayMs(1000)
        },
        ruleStep(id = "excalibur_module", phase = RulePhase.MODULES, order = 60) {
            requiresModule(GameModule.EXCALIBUR)
            clips(ClipId.EXCALIBUR)
            baseDelayMs(1300)
        },
        ruleStep(id = "excalibur_switch", phase = RulePhase.MODULES, order = 61) {
            requiresModule(GameModule.EXCALIBUR)
            clips(ClipId.EXCALIBUR_SWITCH_REMINDER)
            baseDelayMs(1100)
        },
        ruleStep(id = "reminder_merlin", phase = RulePhase.CLOSING, order = 80) {
            requiresNarrationRemindersEnabled()
            requires(RoleId.MERLIN)
            clips(ClipId.ROLE_REMINDER_MERLIN)
            baseDelayMs(900)
        },
        ruleStep(id = "reminder_assassin", phase = RulePhase.CLOSING, order = 81) {
            requiresNarrationRemindersEnabled()
            requires(RoleId.ASSASSIN)
            clips(ClipId.ROLE_REMINDER_ASSASSIN)
            baseDelayMs(900)
        },
        ruleStep(id = "reminder_mordred", phase = RulePhase.CLOSING, order = 82) {
            requiresNarrationRemindersEnabled()
            requires(RoleId.MORDRED)
            clips(ClipId.ROLE_REMINDER_MORDRED)
            baseDelayMs(900)
        },
        ruleStep(id = "reminder_oberon", phase = RulePhase.CLOSING, order = 83) {
            requiresNarrationRemindersEnabled()
            requires(RoleId.OBERON)
            clips(ClipId.ROLE_REMINDER_OBERON)
            baseDelayMs(900)
        },
        ruleStep(id = "reminder_morgana", phase = RulePhase.CLOSING, order = 84) {
            requiresNarrationRemindersEnabled()
            requires(RoleId.MORGANA)
            clips(ClipId.ROLE_REMINDER_MORGANA)
            baseDelayMs(900)
        },
        ruleStep(id = "reminder_percival", phase = RulePhase.CLOSING, order = 85) {
            requiresNarrationRemindersEnabled()
            requires(RoleId.PERCIVAL)
            clips(ClipId.ROLE_REMINDER_PERCIVAL)
            baseDelayMs(900)
        },
        ruleStep(id = "reminder_lunatic", phase = RulePhase.CLOSING, order = 86) {
            requiresNarrationRemindersEnabled()
            requires(RoleId.LUNATIC)
            clips(ClipId.ROLE_REMINDER_LUNATIC)
            baseDelayMs(900)
        },
        ruleStep(id = "reminder_brute", phase = RulePhase.CLOSING, order = 87) {
            requiresNarrationRemindersEnabled()
            requires(RoleId.BRUTE)
            clips(ClipId.ROLE_REMINDER_BRUTE)
            baseDelayMs(900)
        },
        ruleStep(id = "reminder_revealer", phase = RulePhase.CLOSING, order = 88) {
            requiresNarrationRemindersEnabled()
            requires(RoleId.REVEALER)
            clips(ClipId.ROLE_REMINDER_REVEALER)
            baseDelayMs(900)
        },
        ruleStep(id = "reminder_cleric", phase = RulePhase.CLOSING, order = 89) {
            requiresNarrationRemindersEnabled()
            requires(RoleId.CLERIC)
            clips(ClipId.ROLE_REMINDER_CLERIC)
            baseDelayMs(900)
        },
        ruleStep(id = "reminder_trickster", phase = RulePhase.CLOSING, order = 891) {
            requiresNarrationRemindersEnabled()
            requires(RoleId.TRICKSTER)
            clips(ClipId.ROLE_REMINDER_TRICKSTER)
            baseDelayMs(900)
        },
        ruleStep(id = "reminder_troublemaker", phase = RulePhase.CLOSING, order = 892) {
            requiresNarrationRemindersEnabled()
            requires(RoleId.TROUBLEMAKER)
            clips(ClipId.ROLE_REMINDER_TROUBLEMAKER)
            baseDelayMs(900)
        },
        ruleStep(id = "reminder_untrustworthy", phase = RulePhase.CLOSING, order = 893) {
            requiresNarrationRemindersEnabled()
            requires(RoleId.UNTRUSTWORTHY_SERVANT)
            clips(ClipId.ROLE_REMINDER_UNTRUSTWORTHY)
            baseDelayMs(900)
        },
        ruleStep(id = "reminder_rogue", phase = RulePhase.CLOSING, order = 894) {
            requiresNarrationRemindersEnabled()
            requiresAny(RoleId.ROGUE_GOOD, RoleId.ROGUE_EVIL)
            clips(ClipId.ROLE_REMINDER_ROGUE)
            baseDelayMs(900)
        },
        ruleStep(id = "reminder_messengers", phase = RulePhase.CLOSING, order = 895) {
            requiresNarrationRemindersEnabled()
            requiresAny(RoleId.SENIOR_MESSENGER, RoleId.JUNIOR_MESSENGER, RoleId.EVIL_MESSENGER)
            clips(ClipId.ROLE_REMINDER_MESSENGERS)
            baseDelayMs(900)
        },
        ruleStep(id = "reminder_sorcerers", phase = RulePhase.CLOSING, order = 897) {
            requiresNarrationRemindersEnabled()
            requiresAny(RoleId.SORCERER_GOOD, RoleId.SORCERER_EVIL)
            clips(ClipId.ROLE_REMINDER_SORCERERS)
            baseDelayMs(900)
        },
        ruleStep(id = "reminder_lancelots", phase = RulePhase.CLOSING, order = 896) {
            requiresNarrationRemindersEnabled()
            requiresAny(RoleId.LANCELOT_GOOD, RoleId.LANCELOT_EVIL)
            clips(ClipId.ROLE_REMINDER_LANCELOTS)
            baseDelayMs(900)
        },
        ruleStep(id = "closing", phase = RulePhase.CLOSING, order = 900) {
            clips(ClipId.ALL_OPEN, ClipId.GAME_START)
            baseDelayMs(1300)
        },
    )
}
