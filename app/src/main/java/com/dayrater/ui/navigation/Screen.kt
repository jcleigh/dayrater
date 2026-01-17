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
    
    // ==================== Insights Screens ====================
    
    /**
     * Insights hub screen - Entry point for all insights.
     */
    @Serializable
    data object Insights : Screen
    
    /**
     * Weekly summary screen - View aggregated data for a week.
     * @param weekStartEpochDay The first day of the week as epoch day
     */
    @Serializable
    data class WeeklySummary(val weekStartEpochDay: Long) : Screen
    
    /**
     * Category week detail - Daily breakdown for a category in a specific week.
     * @param categoryId The category ID
     * @param weekStartEpochDay The first day of the week as epoch day
     */
    @Serializable
    data class CategoryWeekDetail(val categoryId: Long, val weekStartEpochDay: Long) : Screen
    
    /**
     * Monthly calendar screen - Heat map view for a month.
     */
    @Serializable
    data object MonthlyCalendar : Screen
    
    /**
     * Trends screen - Line graphs of rating history.
     */
    @Serializable
    data object Trends : Screen
    
    /**
     * Statistics screen - Streaks, averages, distribution.
     */
    @Serializable
    data object Statistics : Screen
}

/**
 * Top-level navigation destinations shown in bottom nav bar.
 */
val TopLevelDestinations = listOf(
    Screen.Home,
    Screen.Insights,
    Screen.Calendar,
    Screen.Settings
)
