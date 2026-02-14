# Avalon Narrator (KMP Scaffold)

Kotlin Multiplatform app scaffold for an Avalon narrator with:
- Shared Compose UI (Android + iOS)
- Typed Kotlin role catalog, clip IDs, and narration rules (no JSON manifests)
- Bundled selectable voice packs with default-pack fallback for missing clips
- Real platform playback adapters (`ExoPlayer` on Android, `AVAudioPlayer` on iOS)
- Setup validation warnings, run generation, and playback controls
- Local persistence of last setup/settings

## Structure

- `composeApp/src/commonMain/kotlin/com/avalonnarrator/domain` typed roles, clips, setup models
- `composeApp/src/commonMain/kotlin/com/avalonnarrator/engine` DSL rules, planner, validator
- `composeApp/src/commonMain/kotlin/com/avalonnarrator/playback` resolver/player contracts + implementation
- `composeApp/src/commonMain/kotlin/com/avalonnarrator/ui` setup/settings/narrator screens
- `composeApp/src/commonMain/resources` bundled assets (`audio/<voice_pack_id>`, `images/characters`, `fonts`)

## Asset Conventions

- Character image path: `composeApp/src/commonMain/composeResources/drawable/<image_key>.png`
- Audio path: `audio/<voice_pack_id>/<clip_id>.mp3` under `composeApp/src/commonMain/resources`
- Android build maps `src/commonMain/resources` into app assets automatically.

Voice pack mapping is defined in Kotlin at:
- `composeApp/src/commonMain/kotlin/com/avalonnarrator/domain/audio/VoicePackCatalog.kt`

## Commands

- Compile common shared code:
  - `./gradlew :composeApp:compileCommonMainKotlinMetadata`
- Run iOS simulator tests:
  - `./gradlew :composeApp:iosSimulatorArm64Test`
- Android compile requires local Android SDK configured (`ANDROID_HOME` or `local.properties`).
