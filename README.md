# Announcer for Avalon

This project started after a lot of Avalon game nights with friends.

For both seasoned players and beginners, one recurring problem kept coming up: someone makes a blunder during the opening announcements, and then everyone has to recollect and redistribute role cards right when setup seemed finished.

On top of that, being the announcer is awkward when you also have a role card. You have to keep your voice consistent while checking your own information, and mistakes are easy to make.

So I built a simple app to solve exactly that. You choose the roles you want to play with, and the app dynamically assembles the correct announcer sequence for your setup.

I used AI to help build the app and generate the art and voice lines. Most of it came together in just a day, hooray for AI. It was a great fit for Kotlin Multiplatform given the general simplicity of the app, and because there is no networking requirement, the app works offline.

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

| Screen | Preview |
| --- | --- |
| Setup Home | <img width="220" alt="01_setup_home_character_selection" src="https://github.com/user-attachments/assets/8ac6b640-db72-46a4-a6c6-f2bbd72f64b6" /> |
| Role Preview | <img width="220" alt="02_merlin_role_preview_overlay" src="https://github.com/user-attachments/assets/e9ad7b67-f165-4aeb-87cd-97b276e3cf94" /> |
| Good Roles Grid | <img width="220" alt="03_good_roles_selection_grid" src="https://github.com/user-attachments/assets/8fabee1d-546f-473c-b036-ca589134f2f0" /> |
| Narrator Chamber | <img width="220" alt="12_narrators_chamber_playback_timeline" src="https://github.com/user-attachments/assets/68d4e4bf-b8bd-486c-b7c9-362d475c4664" /> |

### Android

| Screen | Preview |
| --- | --- |
| Setup Home | <img width="220" alt="01_setup_home_character_selection" src="https://github.com/user-attachments/assets/4c49e3f1-e66e-497a-b9f0-1006763e9042" /> |
| Evil Roles Grid | <img width="220" alt="05_evil_roles_selection_grid" src="https://github.com/user-attachments/assets/5fd23907-b66e-43f3-a59f-a3f0a8102c6e" /> |
| Voice Selection | <img width="220" alt="11_voice_selection_screen" src="https://github.com/user-attachments/assets/a8e3d02b-eab4-42a7-b033-27cfb4346612" /> |
| Narrator Chamber | <img width="220" alt="12_narrators_chamber_playback_timeline" src="https://github.com/user-attachments/assets/1a70ad8d-659d-4458-8913-0d1199ac1020" /> |
| Lineup Recommendations | <img width="220" alt="recommendations" src="https://github.com/user-attachments/assets/fca290ff-3655-403c-9df4-49cba9b023c4" />


### Web

| Screen | Preview |
| --- | --- |
| Setup Home | <img width="420" alt="01_setup_home_character_selection_web" src="https://github.com/user-attachments/assets/8013dbc6-32d5-49cc-b437-b29b3493f2dc" /> |
| Role Preview | <img width="420" alt="02_percival_role_preview_overlay_web" src="https://github.com/user-attachments/assets/c55a22a2-008f-4da6-ae05-8854d1f92764" /> |
| Roles And Modules | <img width="420" alt="03_evil_roles_and_modules_selection_web" src="https://github.com/user-attachments/assets/2314238d-18d6-4afe-bcb9-c86d59267db6" /> |
| Narrator Chamber | <img width="420" alt="04_narrators_chamber_playback_timeline_web" src="https://github.com/user-attachments/assets/f0e76d5a-e4ad-4294-bcf1-bf2d559cd1e2" /> |
