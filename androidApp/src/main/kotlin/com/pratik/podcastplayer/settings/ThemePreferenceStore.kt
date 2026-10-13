package com.pratik.podcastplayer.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "podcast_player_settings"
)

/** Persists the user's light/dark theme choice across app launches using DataStore. */
class ThemePreferenceStore(context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val dataStore = context.settingsDataStore

    val theme: StateFlow<ThemePreference> = dataStore.data
        .map { preferences ->
            preferences[THEME_KEY]
                ?.let { stored -> runCatching { ThemePreference.valueOf(stored) }.getOrNull() }
                ?: ThemePreference.System
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = ThemePreference.System
        )

    fun setTheme(preference: ThemePreference) {
        scope.launch {
            dataStore.edit { it[THEME_KEY] = preference.name }
        }
    }

    private companion object {
        val THEME_KEY = stringPreferencesKey("theme_preference")
    }
}
