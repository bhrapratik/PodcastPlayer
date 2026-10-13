# CLAUDE.md

KMP podcast player (Android + iOS, both with real native UI). Searches podcasts via the iTunes Search API, plays episodes parsed from RSS feeds.
Stack: Kotlin Multiplatform, Compose (Android) / SwiftUI (iOS), Koin, Ktor, xmlutil, ExoPlayer (Android), AVPlayer (iOS).

## Commands

| Task          | Command                                                                    |
|---------------|----------------------------------------------------------------------------|
| Build Android | `./gradlew :androidApp:assembleDebug`                                      |
| Unit tests    | `./gradlew :sharedLogic:testAndroidHostTest :sharedUI:testAndroidHostTest` |
| iOS tests     | `./gradlew :sharedLogic:iosSimulatorArm64Test`                             |
| Single test   | append `--tests "com.pratik.podcastplayer.SomeTest"`                       |
| Full check    | `./gradlew build` (no ktlint/detekt configured)                            |
| Build iOS app | `cd iosApp && xcodebuild -scheme iosApp -configuration Debug -destination 'platform=iOS Simulator,id=<UDID>' build` (or open `iosApp/iosApp.xcodeproj` in Xcode) |

**Before saying a task is done:** run the Android build and the `sharedLogic` unit tests. If you touched `sharedLogic` or `iosApp`, also run the iOS unit tests and an `xcodebuild` build so non-Android compilation is verified — don't assume Swift/Kotlin-interop changes compile without actually building.

**Inspecting the Kotlin↔Swift interop surface**: don't guess Swift-side symbol names for `sharedLogic` exports. Build the framework (`./gradlew :sharedLogic:linkDebugFrameworkIosSimulatorArm64`) and read the generated header at `sharedLogic/build/bin/iosSimulatorArm64/debugFramework/SharedLogic.framework/Headers/SharedLogic.h` — it has the exact Swift-callable signatures (including surprises like Kotlin `initKoin` becoming Swift's `doInitKoin`, since Kotlin/Native renames top-level functions starting with `init*` to avoid clashing with Swift's initializer syntax, and a `description` property becoming `description_` because `NSObject` already exports `description()`). Kotlin default parameter values are **not** exposed to Swift — every parameter must be passed explicitly.

## Modules

`androidApp → sharedUI → sharedLogic` and `iosApp → sharedLogic` (one-way, never import upward)

- **sharedLogic**: pure KMP, no Compose. Networking, models, repositories, ViewModels, plus an `ios/` package (iosMain-only) of thin Swift-friendly bridge classes (e.g. `PodcastViewModelBridge`) that wrap `StateFlow`-based APIs in plain callbacks, since Swift can't consume a Kotlin `StateFlow` directly. Most business logic goes here.
- **androidApp**: the real Android UI: Compose screens, navigation, `PodcastPlayer` (ExoPlayer + a `MediaSessionService` for background playback), Android-only Koin module.
- **sharedUI**: wizard scaffolding, not used by the running app. Don't put features here unless asked to migrate UI to Compose Multiplatform.
- **iosApp**: the real iOS UI, written as **native SwiftUI** (not Compose Multiplatform) — `List`/`NavigationStack`/`TabView`, SF Symbols, `.searchable`, native grouped-list Settings-style Profile screen. Uses the `PBXFileSystemSynchronizedRootGroup` project format, so any `.swift` file dropped under `iosApp/iosApp/` is picked up automatically — no manual `.pbxproj` edits needed. Same brand colors as Android (hand-ported as dynamic `Color` values in `Theme/AppTheme.swift`, not literally shared code), but iOS-idiomatic components and layout rather than a pixel clone of the Compose screens. Theme preference uses `UserDefaults` (the native iOS equivalent of Android's DataStore-backed `ThemePreferenceStore`), not literal code sharing. Playback uses a native `AVPlayer` wrapper (`Player/PodcastAudioPlayer.swift`) — foreground playback only; it does **not** have Android's background-service/lock-screen-notification parity (that would mean adding the Background Modes audio capability + entitlements, which hasn't been requested).

## Data flow

```
iTunes API / RSS URL → PodcastApiService (Ktor) → PodcastRepositoryImpl → PodcastViewModel
                                                   ├ searchPodcasts: PodcastDto → Podcast
                                                   └ getEpisodes: XML → RssFeedParser → PodcastEpisode
                                                        │                              │
                                      Compose screens (androidApp, via koinViewModel()) │
                                                        │                              │
                                      SwiftUI screens (iosApp, via PodcastViewModelBridge + PodcastListViewModel ObservableObject)
```
Playback (`PodcastPlayer`/ExoPlayer on Android, `PodcastAudioPlayer`/AVPlayer on iOS) is a separate state chain from `PodcastUiState` on both platforms.

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
- `PodcastPlayer`/`PodcastPlayerUiState` (Android) and `PodcastAudioPlayer`/`PodcastPlayerUiState` (iOS) are platform-only and independent of `PodcastUiState`. Don't route playback through `PodcastViewModel` on either platform.
- Android's `PodcastPlayer` talks to `PlaybackService` (a `MediaSessionService`) via a `MediaController`, not a direct `ExoPlayer` reference — this is what keeps playback alive after the app is backgrounded/swiped away. iOS's `PodcastAudioPlayer` owns its `AVPlayer` directly since it has no equivalent background-service requirement yet.

### iOS (SwiftUI)
- Native SwiftUI idioms over Compose-equivalents: `List`/`Section` instead of hand-rolled `Card` rows, `.searchable` instead of a search button, `NavigationStack`'s automatic back button instead of a custom one, `.redacted(reason: .placeholder)` instead of a custom shimmer brush.
- One `PodcastListViewModel` (wrapping `PodcastViewModelBridge`) and one `PodcastAudioPlayer` are created in `RootTabView` and shared via `.environmentObject(...)` — mirrors the single shared `PodcastViewModel`/`PodcastPlayer` instances Android gets from Koin.
- `sharedLogic`'s Koin container is started once, from `iOSApp.init()` via `InitKoinKt.doInitKoin(additionalModules: [], appDeclaration: nil)`.

### Networking
- The single `HttpClient` serves both the iTunes API (base URL hardcoded in `NetworkClient.baseUrl()`) and arbitrary RSS feed URLs. Don't apply iTunes-specific config (base URL, headers) in a way that breaks RSS fetches.
- RSS episode `id` falls back guid → link → `"$title-$index"`. Keep this; feeds often lack a guid.

### Compose (androidApp)
- Split screens into `*Route` (stateful: gets the ViewModel and player) and `*Screen` (stateless: takes state plus lambdas).
- Collect flows with `collectAsStateWithLifecycle`.

## Known limitations (don't "fix" unless asked)

- Selected podcast is held in `remember { mutableStateOf<Podcast?>() }` in `AppNavigation.kt` and passed alongside a `podcastId` nav arg. It does not survive process death. The proper fix is loading by ID from the repository.
- `PodcastViewModel` is registered as a singleton in `appModule`, so its state is shared across the whole app lifetime.
- iOS playback (`PodcastAudioPlayer`) is foreground-only: no Background Modes audio capability/entitlement, so audio stops once iOS fully suspends the app. No lock-screen/Control-Center now-playing controls either. Android has both via `PlaybackService`.
- No reliable tap/UI-automation tool was available for the iOS Simulator in this environment (no `idb`, no visible Simulator.app GUI window for AppleScript) — iOS screens were verified by temporarily swapping the app's root view and screenshotting, not by tapping through real navigation. If `idb` or a visible Simulator window becomes available, prefer real interaction for iOS UI verification.
