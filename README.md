# Podcast Player

A Kotlin Multiplatform podcast player with real native UI on both platforms. Search for shows
via the iTunes Search API, browse episodes parsed straight from each show's RSS feed, and play
them. On Android, playback keeps running in the background (with system notification controls)
after the app is closed; on iOS, playback is foreground-only for now.

Android and iOS each have their own native UI — Jetpack Compose on Android, SwiftUI on iOS —
sharing the same business logic (networking, search, episode parsing) and the same brand theme,
but built with each platform's own idioms rather than a pixel-for-pixel clone (e.g. iOS uses a
native `List`/`.searchable`/grouped-Settings-style screen instead of mirroring the Compose layout).

## Features

- **Search** — debounced iTunes Search API lookup for podcasts.
- **Episodes** — RSS feed fetched and parsed per podcast (via `xmlutil`), including title,
  description, publish date, and audio URL.
- **Background playback (Android)** — episodes play through a Media3 `ExoPlayer` hosted in a
  foreground `MediaSessionService`, so playback (and a system notification with play/pause
  controls) survives the app being backgrounded or swiped from Recents, not just screen rotation.
- **Playback (iOS)** — a native `AVPlayer` wrapper; foreground playback only today, no
  Background Modes capability or lock-screen controls yet.
- **Theme** — System / Light / Dark, switchable from the Profile tab and persisted across
  launches (Jetpack DataStore on Android, `UserDefaults` on iOS).

## Tech stack

- Kotlin Multiplatform — Jetpack Compose (Android UI), native SwiftUI (iOS UI)
- [Koin](https://insert-koin.io/) for dependency injection (shared on Android; started once from
  iOS's `App.init()` and consumed through a small Swift-friendly bridge layer)
- [Ktor](https://ktor.io/) client for networking, `kotlinx.serialization` for JSON
- [xmlutil](https://github.com/pdvrieze/xmlutil) for RSS/XML parsing
- [Media3](https://developer.android.com/media/media3) (`ExoPlayer` + `MediaSession`) for Android playback
- `AVPlayer` (AVFoundation) for iOS playback
- Jetpack DataStore (Android) / `UserDefaults` (iOS) for local settings persistence

## Project structure

| Module | What's in it |
|---|---|
| [`sharedLogic`](./sharedLogic/src) | Shared, platform-agnostic business logic: networking, data models, the podcast repository, and the `PodcastViewModel`. Pure Kotlin Multiplatform — compiles for Android and iOS. Its `ios/` package holds thin Swift-friendly bridge classes, since Swift can't consume a Kotlin `StateFlow` directly. |
| [`androidApp`](./androidApp/src) | The real Android app: Compose screens (Discover, Podcast details, Profile), navigation, the background playback service, and Android-only Koin wiring. |
| [`sharedUI`](./sharedUI/src) | Compose Multiplatform scaffolding from the project template. Not currently used by the running app — all real screens live in `androidApp`. |
| [`iosApp`](./iosApp/iosApp) | The real iOS app: native SwiftUI screens (Discover, Podcast details, Profile), the `PodcastAudioPlayer` (AVPlayer), and the app's brand theme ported to SwiftUI. |

## Running the app

- **Android**: `./gradlew :androidApp:assembleDebug`, or open the project in Android Studio and
  run the `androidApp` configuration.
- **iOS**: open [`/iosApp`](./iosApp) in Xcode and run it, or build from the CLI:
  `cd iosApp && xcodebuild -scheme iosApp -configuration Debug -destination 'platform=iOS Simulator,name=iPhone 17' build`.

## Running tests

- Android unit tests: `./gradlew :sharedLogic:testAndroidHostTest :sharedUI:testAndroidHostTest`
- iOS tests: `./gradlew :sharedLogic:iosSimulatorArm64Test`
- Run a single test class: append `--tests "FullyQualifiedClassName"` to either command above.

## Notes

- On Android 13+, the app requests notification permission on first launch — it's what lets the
  background-playback notification show up; declining it doesn't stop playback, it just hides
  the notification and lock-screen controls.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
