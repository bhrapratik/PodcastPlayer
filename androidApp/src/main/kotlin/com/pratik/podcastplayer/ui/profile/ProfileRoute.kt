package com.pratik.podcastplayer.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.pratik.podcastplayer.settings.ThemePreferenceStore
import org.koin.compose.koinInject

@Composable
fun ProfileRoute(
    themeStore: ThemePreferenceStore = koinInject()
) {
    val selectedTheme by themeStore.theme.collectAsState()

    ProfileScreen(
        selectedTheme = selectedTheme,
        onThemeSelected = themeStore::setTheme
    )
}
