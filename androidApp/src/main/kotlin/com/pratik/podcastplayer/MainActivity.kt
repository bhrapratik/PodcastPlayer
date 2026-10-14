package com.pratik.podcastplayer

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.pratik.podcastplayer.navigation.AppNavigation
import com.pratik.podcastplayer.settings.ThemePreference
import com.pratik.podcastplayer.settings.ThemePreferenceStore
import com.pratik.podcastplayer.ui.theme.PodcastPlayerTheme
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Needed on Android 13+ for the playback notification that keeps the mini player
        // controllable while the app is backgrounded.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            val themeStore: ThemePreferenceStore = koinInject()
            val themePreference by themeStore.theme.collectAsState()

            val darkTheme = when (themePreference) {
                ThemePreference.System -> isSystemInDarkTheme()
                ThemePreference.Light -> false
                ThemePreference.Dark -> true
            }

            PodcastPlayerTheme(darkTheme = darkTheme) {
                AppNavigation()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    AppNavigation()
}