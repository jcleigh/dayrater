package com.dayrater.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations for DayRater.
 * Uses kotlinx.serialization for type-safe navigation with Compose Navigation.
 */
sealed interface Screen {
    
    /**
     * Home screen - Today's rating view with family member tabs.
     */
    @Serializable
    data object Home : Screen
    
    /**
     * Calendar screen - Browse and select past dates to view/edit ratings.
     */
    @Serializable
    data object Calendar : Screen
    
    /**
     * History screen - View ratings for a specific date.
     * @param dateEpochDay The date as epoch day (days since 1970-01-01)
     */
    @Serializable
    data class History(val dateEpochDay: Long) : Screen
    
    /**
     * Settings screen - Manage categories and family members.
     */
    @Serializable
    data object Settings : Screen
    
    /**
     * Manage categories screen - Add, edit, delete custom categories.
     */
    @Serializable
    data object ManageCategories : Screen
    
    /**
     * Manage family members screen - Add, edit family members.
     */
    @Serializable
    data object ManageFamily : Screen
    
    /**
     * Export screen - Export data to CSV.
     */
    @Serializable
    data object Export : Screen
}

/**
 * Top-level navigation destinations shown in bottom nav bar.
 */
val TopLevelDestinations = listOf(
    Screen.Home,
    Screen.Calendar,
    Screen.Settings
)
