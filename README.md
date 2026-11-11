# Eos Lund for Android

A Kotlin app for following Eos Lund basketball. Fans can browse fixtures and results by league, open match details and standings, look up teams and players, and read club news. The app also supports push notifications through Firebase Cloud Messaging.

## Code overview

The Android app lives in [`app/`](app). It uses AndroidX views and fragments with XML layouts, Kotlin coroutines, Cloud Firestore for game data, and Realm for local team and player data.

- [GamesFragment.kt](app/src/main/java/se/eoslund/piggest/controller/GamesFragment.kt) loads fixtures, filters them by league, and opens the standings.
- [MatchActivity.kt](app/src/main/java/se/eoslund/piggest/controller/MatchActivity.kt) brings together the match summary, player list, and statistics.
- [EosFirebaseMessagingService.kt](app/src/main/java/se/eoslund/piggest/services/EosFirebaseMessagingService.kt) handles incoming push notifications.

## Local setup

Open the repository root in Android Studio. The project uses Android Gradle Plugin 7.2.1 and the Gradle 7.3.3 wrapper. Select JDK 11 for Gradle and install Android SDK Platform 32. The minimum device version is Android 8.0 (API 26).

Once Gradle has synced, run this command from the repository root to build the debug APK.

```sh
./gradlew :app:assembleDebug
```

Live fixtures and push notifications require an authorized Firebase backend and matching Google Services configuration. The checked-in project uses older dependencies and includes JCenter in its repository configuration, so Gradle sync may require dependency or repository updates.
