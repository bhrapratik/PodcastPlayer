# Podcast Player

A Kotlin Multiplatform podcast player. Search for shows via the iTunes Search API, browse
episodes parsed straight from each show's RSS feed, and play them with background playback
that keeps running (with system notification controls) after the app is closed.

Android has the full experience today; iOS currently ships the default Kotlin Multiplatform
wizard screen and doesn't have the podcast UI yet.

## Features

- **Search** — debounced iTunes Search API lookup for podcasts.
- **Episodes** — RSS feed fetched and parsed per podcast (via `xmlutil`), including title,
  description, publish date, and audio URL.
- **Background playback** — episodes play through a Media3 `ExoPlayer` hosted in a foreground
  `MediaSessionService`, so playback (and a system notification with play/pause controls)
  survives the app being backgrounded or swiped from Recents, not just screen rotation.
- **Theme** — System / Light / Dark, switchable from the Profile tab and persisted across
  launches with Jetpack DataStore.

## Tech stack

- Kotlin Multiplatform + Jetpack Compose (Android UI)
- [Koin](https://insert-koin.io/) for dependency injection
- [Ktor](https://ktor.io/) client for networking, `kotlinx.serialization` for JSON
- [xmlutil](https://github.com/pdvrieze/xmlutil) for RSS/XML parsing
- [Media3](https://developer.android.com/media/media3) (`ExoPlayer` + `MediaSession`) for playback
- Jetpack DataStore for local settings persistence

## Project structure

| Module | What's in it |
|---|---|
| [`sharedLogic`](./sharedLogic/src) | Shared, platform-agnostic business logic: networking, data models, the podcast repository, and the `PodcastViewModel`. Pure Kotlin Multiplatform — compiles for Android and iOS. |
| [`androidApp`](./androidApp/src) | The real Android app: Compose screens (Discover, Podcast details, Profile), navigation, the background playback service, and Android-only Koin wiring. |
| [`sharedUI`](./sharedUI/src) | Compose Multiplatform scaffolding from the project template. Not currently used by the running app — all real screens live in `androidApp`. |
| [`iosApp`](./iosApp/iosApp) | The iOS app entry point. Currently still the default wizard screen; no podcast UI yet. |

## Running the app

- **Android**: `./gradlew :androidApp:assembleDebug`, or open the project in Android Studio and
  run the `androidApp` configuration.
- **iOS**: open [`/iosApp`](./iosApp) in Xcode and run it from there.

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
