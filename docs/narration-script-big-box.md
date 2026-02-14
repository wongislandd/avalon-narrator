# Avalon Big Box Narration Script Spec

Source ruleset: Avalon Big Box Edition Rulebook (Don Eskridge).

This is a production-oriented script map for dynamic narration.
It is written as modular lines so each line can become a future voice clip.

## 1) Core Model

Each line should be stored as:

- `id`: stable clip key
- `phase`: setup / reveal / team_build / vote / quest / endgame / module
- `when`: predicate over selected roles/modules/options
- `text`: spoken line
- `blocking`: whether game should pause for table action

## 2) Role And Module Flags

Use boolean flags:

- Roles:
`merlin`, `assassin`, `mordred`, `oberon`, `morgana`, `percival`, `lancelot_good`, `lancelot_evil`,
`lunatic`, `brute`, `revealer`, `cleric`, `trickster`, `troublemaker`,
`messenger_senior`, `messenger_junior`, `messenger_evil`,
`untrustworthy_servant`, `rogue_good`, `rogue_evil`, `sorcerer_good`, `sorcerer_evil`
- Modules/options:
`module_lancelot`, `module_rogue`, `module_sorcerer`, `module_messengers`, `module_lady`, `module_excalibur`, `module_plot_cards`
- Rule options:
`lancelot_variant_no_counterpart_reveal`, `lancelot_variant_thumb_evil_lancelot`,
`sorcerer_option_evil_hidden`, `messenger_option_senior_knows_junior`

## 3) Reveal Stage Script (Composable)

Always start:

- `reveal_000`: "Everyone, close your eyes and make a fist in front of you."

Cleric block (if `cleric`):

- `reveal_010`: "Leader, if you are evil, extend your thumb now."
- `reveal_011`: "Cleric, open your eyes."
- `reveal_012`: "Cleric, close your eyes."
- `reveal_013`: "Leader, return your hand to a fist."

Evil mutual awareness block:

- `reveal_020`: "Minions of Mordred, open your eyes and look for your allies."
- `reveal_021`: "Minions of Mordred, close your eyes."

Merlin information block:

- `reveal_030`: "All players keep eyes closed and hands in a fist."
- `reveal_031`: "Minions of Mordred, extend your thumb so Merlin can detect evil."
- `reveal_032`: "Merlin, open your eyes and observe the agents of evil."
- `reveal_033`: "Minions of Mordred, return your hand to a fist."
- `reveal_034`: "Merlin, close your eyes."
- `reveal_035`: "All players keep eyes closed and hands in a fist."

Percival and Morgana block (if `percival` and `morgana`):

- `reveal_040`: "Merlin and Morgana, extend your thumb."
- `reveal_041`: "Percival, open your eyes and note Merlin and Morgana."
- `reveal_042`: "Merlin and Morgana, return your hand to a fist."
- `reveal_043`: "Percival, close your eyes."

Lancelot counterpart block (default, if both Lancelots and not `lancelot_variant_no_counterpart_reveal`):

- `reveal_050`: "Lancelot players, open your eyes to find each other."
- `reveal_051`: "Lancelot players, close your eyes."

Messenger optional block (if `module_messengers` and `messenger_option_senior_knows_junior`):

- `reveal_060`: "Junior Messenger, extend your thumb so the Senior Messenger can identify you."
- `reveal_061`: "Senior Messenger, open your eyes."
- `reveal_062`: "Senior Messenger, close your eyes."
- `reveal_063`: "Junior Messenger, return your hand to a fist."

Untrustworthy Servant block (if `untrustworthy_servant`):

- `reveal_070`: "Minions of Mordred and the Untrustworthy Servant, extend your thumb for Merlin."
- `reveal_071`: "Minions of Mordred and the Untrustworthy Servant, return your hand to a fist."
- `reveal_072`: "Assassin, extend your thumb so the Untrustworthy Servant can identify you."
- `reveal_073`: "Untrustworthy Servant, open your eyes."
- `reveal_074`: "Untrustworthy Servant, close your eyes."
- `reveal_075`: "Assassin, return your hand to a fist."

Always end:

- `reveal_999`: "Everyone, open your eyes."

## 4) Reveal Stage Role-Specific Filters

These rules modify the reveal lines above.

- Mordred:
  - Excluded from what Merlin learns (`reveal_032` logic).
- Oberon:
  - Does not join evil mutual awareness (`reveal_020`).
  - Does not know evil allies.
- Lancelot Variant: Evil Lancelot thumb mode (`lancelot_variant_thumb_evil_lancelot`):
  - Remove evil mutual awareness eye-open for Evil Lancelot.
  - Add:
    - `reveal_lan_001`: "Evil Lancelot, extend your thumb so evil can identify you."
    - `reveal_lan_002`: "Minions of Mordred except Evil Lancelot, open your eyes and identify allies."
    - `reveal_lan_003`: "Evil Lancelot, return your hand to a fist."
- Rogue module:
  - Evil Rogue does not open eyes in evil mutual awareness.
  - Evil Rogue does not extend thumb for Merlin.
- Sorcerer optional hidden evil (`sorcerer_option_evil_hidden`):
  - Evil Sorcerer does not reveal to evil in mutual awareness block.
- Cleric:
  - Learns only whether current Leader is good or evil, not exact role.
- Trickster:
  - May lie when checked by Cleric/loyalty effects (no reveal-stage line change).
- Troublemaker:
  - Must lie when checked by Cleric/loyalty effects (no reveal-stage line change).

## 5) Character Power Prompts (In-Game Narration)

These are optional reminder lines you can play once at game start if role is present.

- `rem_merlin`: "Merlin knows most evil players but must stay hidden."
- `rem_assassin`: "If good gets three successful quests, Assassin makes one final guess for Merlin."
- `rem_mordred`: "Mordred stays hidden from Merlin."
- `rem_oberon`: "Oberon is evil, but does not coordinate with other evil players."
- `rem_morgana`: "Morgana appears as Merlin to Percival."
- `rem_percival`: "Percival sees Merlin and Morgana, but cannot distinguish them."
- `rem_lunatic`: "Lunatic must fail every quest they join."
- `rem_brute`: "Brute may fail only the first three quests."
- `rem_revealer`: "Revealer must reveal identity after the second failed quest."
- `rem_cleric`: "Cleric learns alignment of the Leader during reveal."
- `rem_trickster`: "Trickster may lie when loyalty is checked."
- `rem_troublemaker`: "Troublemaker must lie when loyalty is checked."
- `rem_untrustworthy`: "Untrustworthy Servant knows Assassin, and may be recruited after three successful quests."
- `rem_rogue`: "Rogue players pursue personal win conditions through Rogue quest cards."
- `rem_sorcerer`: "Sorcerers can reverse quest outcomes using Magic cards."
- `rem_messengers`: "Messengers can add backup effects with message quest cards."

## 6) Core Round Narration

### Team Build

- `round_010`: "Leader, choose your team for this quest."
- `round_011`: "Discuss openly. When ready, lock the team."

### Team Vote

- `vote_010`: "All players, choose approve or reject."
- `vote_011`: "Reveal votes."
- `vote_012_approved`: "Team approved. Proceed to quest."
- `vote_013_rejected`: "Team rejected. Leadership passes clockwise."
- `vote_014_five_rejects`: "Five consecutive team rejections. Evil wins immediately."

### Quest Resolution

- `quest_010`: "Quest team, secretly select your quest card."
- `quest_011`: "Leader, collect and shuffle submitted cards."
- `quest_012_reveal`: "Reveal quest result now."
- `quest_013_success`: "Quest succeeds."
- `quest_014_fail`: "Quest fails."
- `quest_015_double_fail_rule`: "On this quest, two fails are required to fail."

## 7) Module/Variant Runtime Prompts

### Lancelot Allegiance Variant

- `lan_100_setup`: "Allegiance deck enabled. Lancelot allegiance may change during the game."
- `lan_101_draw`: "Draw and reveal the allegiance card for this quest."
- `lan_102_switch`: "Lancelots, switch allegiance now, in secret."
- `lan_103_variant2_note`: "Variant two active: Evil Lancelot must play fail while evil."

### Rogue Module

- `rog_100_setup`: "Rogue module active. Rogue quest cards are in circulation."
- `rog_101_watch`: "Leader, assign the Watch token to one team member."
- `rog_102_token_success`: "Place one Rogue Success token."
- `rog_103_token_fail`: "Place one Rogue Fail token."
- `rog_104_good_win`: "Good Rogue achieves personal victory."
- `rog_105_evil_win`: "Evil Rogue achieves personal victory."
- `rog_106_skip_assassination`: "A Rogue achieved personal victory. Skip assassination."

### Sorcerer Module

- `sor_100_setup`: "Sorcerer module active. Magic cards are available on quests."
- `sor_101_play`: "Quest team, include magic cards if eligible."
- `sor_102_resolve`: "Resolve magic reversals. Odd magic reverses result, even cancels out."

### Messengers Module

- `msg_100_setup`: "Messengers module active. Message cards are added to quest hands."
- `msg_101_backup_good`: "Good backup is active for this quest."
- `msg_102_backup_evil`: "Evil backup is active for this quest."
- `msg_103_assassin_choice`: "Assassin may target Merlin, or the messenger pair."
- `msg_104_assassin_pair_check`: "To target messengers, name both good messengers."

### Lady of the Lake

- `lady_100_setup`: "Lady of the Lake starts to the Leader's right."
- `lady_101_trigger`: "Lady of the Lake holder, choose one player to check."
- `lady_102_pass`: "Pass Lady of the Lake token to the checked player."
- `lady_103_restrict`: "You cannot target a player who has previously held Lady of the Lake."

### Excalibur

- `exc_100_setup`: "Leader, assign Excalibur to a team member."
- `exc_101_window`: "Before quest cards are collected, Excalibur holder may switch one player’s quest card."
- `exc_102_after`: "Excalibur holder, privately check the replaced card."

### Plot Cards

- `plot_100_draw`: "Leader, draw and distribute plot cards for this quest."
- `plot_101_play_window`: "Plot card window is open. Resolve immediate plot cards first."
- `plot_102_secret`: "Discuss information, but do not reveal quest or loyalty cards."

## 8) Endgame Narration

- `end_010_good_three_success`: "Good has completed three successful quests."
- `end_011_evil_three_fail`: "Evil has completed three failed quests."
- `end_012_assassination_start`: "Assassination stage begins. Assassin, choose your target."
- `end_013_assassin_correct`: "Assassin identified Merlin. Evil wins."
- `end_014_assassin_wrong`: "Assassin missed Merlin. Good wins."

Untrustworthy Servant recruitment stage (`untrustworthy_servant` and three successful quests):

- `end_020_recruit_start`: "Recruitment stage: Assassin, name who you believe is the Untrustworthy Servant."
- `end_021_recruit_success`: "Recruitment succeeded. The Untrustworthy Servant joins evil and performs assassination."
- `end_022_recruit_fail`: "Recruitment failed. Continue with normal assassination."

## 9) Minimal Build Order For Voice Production

Produce in this order:

1. Reveal base lines (`reveal_000`, `reveal_020..035`, `reveal_999`)
2. Role modifiers (Mordred/Oberon/Morgana/Percival/Lancelot)
3. Core round lines (`round_*`, `vote_*`, `quest_*`, `end_*`)
4. Module packs (`lady_*`, `exc_*`, `msg_*`, `rog_*`, `sor_*`, `plot_*`)
5. Reminder lines (`rem_*`)

This keeps the first shipping voice pack usable for standard Avalon + common special roles.
