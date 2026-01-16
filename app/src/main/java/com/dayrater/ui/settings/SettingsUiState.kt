package com.dayrater.ui.settings

import com.dayrater.data.repository.ThemeMode

/**
 * UI state for the settings screen.
 */
data class SettingsUiState(
    val isLoading: Boolean = false,
    val familyMemberCount: Int = 1,
    val categoryCount: Int = 4,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val showThemeDialog: Boolean = false,
    val appVersion: String = "1.0.0",
    val error: String? = null
)

/**
 * Events that can be sent from the settings screen.
 */
sealed interface SettingsEvent {
    data object NavigateToManageCategories : SettingsEvent
    data object NavigateToManageFamily : SettingsEvent
    data object NavigateToExport : SettingsEvent
    data object ShowThemeDialog : SettingsEvent
    data object DismissThemeDialog : SettingsEvent
    data class SetThemeMode(val mode: ThemeMode) : SettingsEvent
    data object DismissError : SettingsEvent
}
