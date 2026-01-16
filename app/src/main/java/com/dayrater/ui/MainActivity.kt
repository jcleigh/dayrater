package com.dayrater.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dayrater.data.repository.ThemeMode
import com.dayrater.ui.navigation.DayRaterBottomNavBar
import com.dayrater.ui.navigation.DayRaterNavGraph
import com.dayrater.ui.navigation.Screen
import com.dayrater.ui.navigation.TopLevelDestinations
import com.dayrater.ui.theme.DayRaterTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for DayRater.
 * Sets up the app scaffold with bottom navigation and hosts the navigation graph.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DayRaterApp()
        }
    }
}

/**
 * Root composable for the DayRater app.
 */
@Composable
fun DayRaterApp(
    viewModel: MainViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val isDarkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    
    DayRaterTheme(darkTheme = isDarkTheme) {
        DayRaterContent()
    }
}

/**
 * Main content composable with navigation.
 */
@Composable
private fun DayRaterContent() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    
    // Determine current screen for bottom nav highlighting
    var currentScreen by rememberSaveable { mutableStateOf<Screen>(Screen.Home) }
    
    // Update current screen based on navigation
    navBackStackEntry?.destination?.route?.let { route ->
        currentScreen = when {
            route.contains("Home") -> Screen.Home
            route.contains("Calendar") -> Screen.Calendar
            route.contains("Settings") -> Screen.Settings
            else -> currentScreen
        }
    }
    
    // Show bottom nav only on top-level destinations
    val showBottomNav = navBackStackEntry?.destination?.route?.let { route ->
        TopLevelDestinations.any { destination ->
            route.contains(destination::class.simpleName ?: "")
        }
    } ?: true
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomNav) {
                DayRaterBottomNavBar(
                    currentScreen = currentScreen,
                    onNavigate = { screen ->
                        navController.navigate(screen) {
                            // Pop up to the start destination to avoid building up a large stack
                            popUpTo(Screen.Home) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when re-selecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        DayRaterNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
