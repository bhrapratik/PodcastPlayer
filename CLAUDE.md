# CLAUDE.md

KMP podcast player (Android + iOS). Searches podcasts via the iTunes Search API, plays episodes parsed from RSS feeds.
Stack: Kotlin Multiplatform, Compose (Android), Koin, Ktor, xmlutil, ExoPlayer (Android only).

## Commands

| Task          | Command                                                                    |
|---------------|----------------------------------------------------------------------------|
| Build Android | `./gradlew :androidApp:assembleDebug`                                      |
| Unit tests    | `./gradlew :sharedLogic:testAndroidHostTest :sharedUI:testAndroidHostTest` |
| iOS tests     | `./gradlew :sharedLogic:iosSimulatorArm64Test`                             |
| Single test   | append `--tests "com.pratik.podcastplayer.SomeTest"`                       |
| Full check    | `./gradlew build` (no ktlint/detekt configured)                            |
| iOS app       | open `iosApp/` in Xcode and run (no CLI build)                             |

**Before saying a task is done:** run the Android build and the `sharedLogic` unit tests. If you touched `sharedLogic`, also run the iOS tests so non-Android compilation is verified.

## Modules

`androidApp → sharedUI → sharedLogic` (one-way, never import upward)

- **sharedLogic**: pure KMP, no Compose. Networking, models, repositories, ViewModels. Most logic goes here.
- **androidApp**: the real Android UI: screens, navigation, `PodcastPlayer` (ExoPlayer), Android-only Koin module.
- **sharedUI**: wizard scaffolding, not used by the running app. Don't put features here unless asked to migrate UI to Compose Multiplatform.
- **iosApp**: wizard template only. iOS UI = SwiftUI consuming `SharedLogic.framework`.

## Data flow

```
iTunes API / RSS URL → PodcastApiService (Ktor) → PodcastRepositoryImpl → PodcastViewModel → Compose screens (androidApp)
                                                   ├ searchPodcasts: PodcastDto → Podcast
                                                   └ getEpisodes: XML → RssFeedParser → PodcastEpisode
PodcastPlayer (androidApp) is separate from this chain.
```

## Rules

### sharedLogic
- No `android.*`, `androidx.*` (except shared lifecycle-viewmodel), or `java.*` imports in `commonMain`. It must compile for iOS.
- `PodcastViewModel` stays platform-agnostic, because it will back iOS screens.
- Data models: DTOs (`*Dto`) map to domain models in the repository; UI never sees DTOs.

### Adding async data to a screen
Follow the existing `PodcastViewModel` pattern:
1. A private state data class (like `PodcastSearchState` / `EpisodeState`)
2. A flow pipeline with `.onStart { emit(loading) }` and `.catch { emit(error) }`
3. Fold it into `PodcastUiState` via `combine()`

### Errors
- Map exceptions to user messages only in `Throwable.toUserMessage` (`sharedLogic/network/ExceptionHandler.kt`). No inline message strings in ViewModels.

### DI (Koin)
- Shared deps go in `appModule` (`sharedLogic/di/AppModule.kt`).
- Android-only deps (anything needing `Context`, ExoPlayer) go in `androidModule` (`androidApp/di/AndroidModule.kt`).
- Don't introduce Hilt or another DI framework.

### Local persistence
- Use Jetpack DataStore (`androidx.datastore:datastore-preferences`) for local key-value settings (see `ThemePreferenceStore` in `androidApp/settings`). Don't use `SharedPreferences` for new persistence.

### Player
- `PodcastPlayer` and `PodcastPlayerUiState` are Android-only and independent of `PodcastUiState`. Screens read them via `koinInject()`; don't route playback through `PodcastViewModel`.

### Networking
- The single `HttpClient` serves both the iTunes API (base URL hardcoded in `NetworkClient.baseUrl()`) and arbitrary RSS feed URLs. Don't apply iTunes-specific config (base URL, headers) in a way that breaks RSS fetches.
- RSS episode `id` falls back guid → link → `"$title-$index"`. Keep this; feeds often lack a guid.

### Compose (androidApp)
- Split screens into `*Route` (stateful: gets the ViewModel and player) and `*Screen` (stateless: takes state plus lambdas).
- Collect flows with `collectAsStateWithLifecycle`.

## Known limitations (don't "fix" unless asked)

- Selected podcast is held in `remember { mutableStateOf<Podcast?>() }` in `AppNavigation.kt` and passed alongside a `podcastId` nav arg. It does not survive process death. The proper fix is loading by ID from the repository.
- `PodcastViewModel` is registered as a singleton in `appModule`, so its state is shared across the whole app lifetime.
- No iOS feature screens yet.
