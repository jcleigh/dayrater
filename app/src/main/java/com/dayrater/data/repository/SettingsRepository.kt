package com.dayrater.data.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for app settings and preferences.
 * Uses DataStore for persistent key-value storage.
 */
interface SettingsRepository {
    
    /**
     * The current theme mode preference.
     */
    val themeMode: Flow<ThemeMode>
    
    /**
     * Set the theme mode preference.
     * 
     * @param mode The theme mode to set
     */
    suspend fun setThemeMode(mode: ThemeMode)
}

/**
 * Theme mode options for the app.
 */
enum class ThemeMode {
    /**
     * Follow system theme setting.
     */
    SYSTEM,
    
    /**
     * Always use light theme.
     */
    LIGHT,
    
    /**
     * Always use dark theme.
     */
    DARK
}
