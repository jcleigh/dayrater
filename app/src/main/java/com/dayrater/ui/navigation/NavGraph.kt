package com.dayrater.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dayrater.ui.export.ExportScreen
import com.dayrater.ui.history.DayDetailScreen
import com.dayrater.ui.history.HistoryScreen
import com.dayrater.ui.insights.CategoryWeekDetailScreen
import com.dayrater.ui.insights.InsightsScreen
import com.dayrater.ui.insights.MonthlyCalendarScreen
import com.dayrater.ui.insights.StatisticsScreen
import com.dayrater.ui.insights.TrendsScreen
import com.dayrater.ui.insights.WeeklySummaryScreen
import com.dayrater.ui.rating.RatingScreen
import com.dayrater.ui.settings.CustomCategoriesScreen
import com.dayrater.ui.settings.FamilySetupScreen
import com.dayrater.ui.settings.SettingsScreen
import java.time.LocalDate

/**
 * Main navigation graph for DayRater.
 * 
 * @param navController The navigation controller
 * @param modifier Modifier for the NavHost
 */
@Composable
fun DayRaterNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home,
        modifier = modifier
    ) {
        // Home - Today's ratings
        composable<Screen.Home> {
            RatingScreen()
        }
        
        // Calendar - Browse dates
        composable<Screen.Calendar> {
            HistoryScreen(
                onDateSelected = { date ->
                    navController.navigateToHistory(date)
                }
            )
        }
        
        // History - View ratings for a specific date
        composable<Screen.History> {
            DayDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Settings - Main settings screen
        composable<Screen.Settings> {
            SettingsScreen(
                onNavigateToManageCategories = { navController.navigateToManageCategories() },
                onNavigateToManageFamily = { navController.navigateToManageFamily() },
                onNavigateToExport = { navController.navigateToExport() }
            )
        }
        
        // Manage Categories
        composable<Screen.ManageCategories> {
            CustomCategoriesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Manage Family Members
        composable<Screen.ManageFamily> {
            FamilySetupScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Export
        composable<Screen.Export> {
            ExportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // ==================== Insights Screens ====================
        
        // Insights Hub
        composable<Screen.Insights> {
            InsightsScreen(
                onNavigateToWeeklySummary = { navController.navigateToWeeklySummary() },
                onNavigateToMonthlyCalendar = { navController.navigate(Screen.MonthlyCalendar) },
                onNavigateToTrends = { navController.navigate(Screen.Trends) },
                onNavigateToStatistics = { navController.navigate(Screen.Statistics) },
                onNavigateToHistory = { navController.navigate(Screen.Calendar) }
            )
        }
        
        // Weekly Summary
        composable<Screen.WeeklySummary> {
            WeeklySummaryScreen(
                onNavigateBack = { navController.popBackStack() },
                onCategoryClick = { categoryId, weekStartEpochDay ->
                    navController.navigateToCategoryWeekDetail(categoryId, weekStartEpochDay)
                }
            )
        }
        
        // Category Week Detail
        composable<Screen.CategoryWeekDetail> {
            CategoryWeekDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Monthly Calendar
        composable<Screen.MonthlyCalendar> {
            MonthlyCalendarScreen(
                onNavigateBack = { navController.popBackStack() },
                onDayClick = { date ->
                    navController.navigateToHistory(date)
                }
            )
        }
        
        // Trends
        composable<Screen.Trends> {
            TrendsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Statistics
        composable<Screen.Statistics> {
            StatisticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Extension function to navigate to a date's history.
 */
fun NavHostController.navigateToHistory(date: LocalDate) {
    navigate(Screen.History(date.toEpochDay()))
}

/**
 * Extension function to navigate to manage categories.
 */
fun NavHostController.navigateToManageCategories() {
    navigate(Screen.ManageCategories)
}

/**
 * Extension function to navigate to manage family.
 */
fun NavHostController.navigateToManageFamily() {
    navigate(Screen.ManageFamily)
}

/**
 * Extension function to navigate to export.
 */
fun NavHostController.navigateToExport() {
    navigate(Screen.Export)
}

/**
 * Extension function to navigate to weekly summary for the current week.
 */
fun NavHostController.navigateToWeeklySummary(weekStartEpochDay: Long? = null) {
    val epochDay = weekStartEpochDay ?: LocalDate.now().toEpochDay()
    navigate(Screen.WeeklySummary(epochDay))
}

/**
 * Extension function to navigate to category week detail.
 */
fun NavHostController.navigateToCategoryWeekDetail(categoryId: Long, weekStartEpochDay: Long) {
    navigate(Screen.CategoryWeekDetail(categoryId, weekStartEpochDay))
}
