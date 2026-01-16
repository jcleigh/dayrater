package com.dayrater.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dayrater.ui.history.DayDetailScreen
import com.dayrater.ui.history.HistoryScreen
import com.dayrater.ui.rating.RatingScreen
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
            // TODO: Implement ManageCategoriesScreen
            PlaceholderScreen(title = "Manage Categories")
        }
        
        // Manage Family Members
        composable<Screen.ManageFamily> {
            FamilySetupScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Export
        composable<Screen.Export> {
            // TODO: Implement ExportScreen
            PlaceholderScreen(title = "Export Data")
        }
    }
}

/**
 * Temporary placeholder screen for unimplemented destinations.
 */
@Composable
private fun PlaceholderScreen(title: String) {
    androidx.compose.material3.Surface {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier,
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material3.Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
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
