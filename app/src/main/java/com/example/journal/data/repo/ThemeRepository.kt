package com.example.journal.data.repo

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create a single DataStore instance on the Context
private val Context.dataStore by preferencesDataStore(name = "settings")

class ThemeRepository(private val context: Context) {

    companion object {
        private val KEY_IS_DARK = booleanPreferencesKey("is_dark_theme")
    }

    /**
     * Flow that emits the current theme preference.
     * Defaults to false (light theme) if value not set.
     */
    val isDarkThemeFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs: Preferences ->
            prefs[KEY_IS_DARK] ?: false
        }

    /**
     * Persist the theme preference.
     */
    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_DARK] = isDark
        }
    }
}
