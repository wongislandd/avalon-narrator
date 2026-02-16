# Announcer for Avalon

This project started after a lot of Avalon game nights with friends.

For both seasoned players and beginners, one recurring problem kept coming up: someone makes a blunder during the opening announcements, and then everyone has to recollect and redistribute role cards right when setup seemed finished.

On top of that, being the announcer is awkward when you also have a role card. You have to keep your voice consistent while checking your own information, and mistakes are easy to make.

So I built a simple app to solve exactly that. You choose the roles you want to play with, and the app dynamically assembles the correct announcer sequence for your setup.

I used AI to help build the app and generate the art and voice lines. Everything came together in about a day. It was a great fit for Kotlin Multiplatform, and because there is no networking requirement, the app works offline.

## What The App Does

- Lets players configure their exact role and module setup
- Validates setup rules before narration starts
- Builds a dynamic, deterministic announcement timeline
- Plays narration with timing controls and voice pack support
- Keeps everything local/offline for reliability at the table

## Current Feature Set

- Shared Kotlin Multiplatform app for Android, iOS, and Web (Wasm)
- Role selection for good and evil teams with quantity controls for base roles
- Module toggles (including Excalibur and Lady of the Lake)
- Validation panel for blocking errors and non-blocking warnings
- Long-press role previews and module guidance overlays
- Lineup Codex recommendations by player count
- Settings for pause durations, reminders, and rule enforcement
- Voice pack selection with preview playback and fallback clip resolution
- Narrator chamber playback timeline with play/pause/step/restart controls
- Local persistence of setup and settings across runs
- Fully offline operation (no backend/network dependency)

## Screenshot Gallery

### iOS

![iOS Setup Home](/Users/wongislandd/Desktop/avalon_ios/01_setup_home_character_selection.png)
![iOS Role Preview](/Users/wongislandd/Desktop/avalon_ios/02_merlin_role_preview_overlay.png)
![iOS Good Roles Grid](/Users/wongislandd/Desktop/avalon_ios/03_good_roles_selection_grid.png)
![iOS Narrator Chamber](/Users/wongislandd/Desktop/avalon_ios/12_narrators_chamber_playback_timeline.png)

### Android

![Android Setup Home](/Users/wongislandd/Desktop/avalon_android/01_setup_home_character_selection.png)
![Android Evil Roles](/Users/wongislandd/Desktop/avalon_android/05_evil_roles_selection_grid.png)
![Android Voice Selection](/Users/wongislandd/Desktop/avalon_android/11_voice_selection_screen.png)
![Android Narrator Chamber](/Users/wongislandd/Desktop/avalon_android/12_narrators_chamber_playback_timeline.png)

### Web

![Web Setup Home](/Users/wongislandd/Desktop/avalon_web/01_setup_home_character_selection_web.png)
![Web Role Preview](/Users/wongislandd/Desktop/avalon_web/02_percival_role_preview_overlay_web.png)
![Web Roles And Modules](/Users/wongislandd/Desktop/avalon_web/03_evil_roles_and_modules_selection_web.png)
![Web Narrator Chamber](/Users/wongislandd/Desktop/avalon_web/04_narrators_chamber_playback_timeline_web.png)