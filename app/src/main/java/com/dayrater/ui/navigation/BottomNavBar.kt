package com.dayrater.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.dayrater.R

/**
 * Navigation item data class for bottom navigation bar.
 */
data class BottomNavItem(
    val screen: Screen,
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val contentDescription: String
)

/**
 * Bottom navigation items for the app.
 */
val bottomNavItems = listOf(
    BottomNavItem(
        screen = Screen.Home,
        labelResId = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        contentDescription = "Home"
    ),
    BottomNavItem(
        screen = Screen.Calendar,
        labelResId = R.string.nav_calendar,
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
        contentDescription = "Calendar"
    ),
    BottomNavItem(
        screen = Screen.Insights,
        labelResId = R.string.nav_insights,
        selectedIcon = Icons.Filled.Insights,
        unselectedIcon = Icons.Outlined.Insights,
        contentDescription = "Insights"
    ),
    BottomNavItem(
        screen = Screen.Settings,
        labelResId = R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        contentDescription = "Settings"
    )
)

/**
 * Bottom navigation bar for DayRater.
 * 
 * @param currentScreen The currently selected screen
 * @param onNavigate Callback when a navigation item is clicked
 * @param modifier Modifier for the navigation bar
 */
@Composable
fun DayRaterBottomNavBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        bottomNavItems.forEach { item ->
            val selected = currentScreen == item.screen
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.screen) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.contentDescription
                    )
                },
                label = {
                    Text(text = stringResource(item.labelResId))
                }
            )
        }
    }
}
