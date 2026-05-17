package com.example.chatty_android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.chatty_android.common.constants.Constants
import com.example.chatty_android.ui.theme.ThemeColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_THEME_COLOR = stringPreferencesKey(Constants.KEY_THEME_COLOR)
        private val KEY_DARK_THEME = booleanPreferencesKey(Constants.KEY_DARK_THEME)
    }

    val themeColor: Flow<ThemeColor> = dataStore.data.map { prefs ->
        val name = prefs[KEY_THEME_COLOR] ?: ThemeColor.CHINESE_RED.name
        try {
            ThemeColor.valueOf(name)
        } catch (_: Exception) {
            ThemeColor.CHINESE_RED
        }
    }

    val isDarkTheme: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_DARK_THEME] ?: false
    }

    suspend fun setThemeColor(themeColor: ThemeColor) {
        dataStore.edit { it[KEY_THEME_COLOR] = themeColor.name }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { it[KEY_DARK_THEME] = enabled }
    }
}
