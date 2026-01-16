# Navigation Contract: Daily Rating Core

**Feature**: 001-daily-rating  
**Date**: 2026-01-16

Defines the app's screen navigation structure using Compose Navigation type-safe routes.

---

## Navigation Graph

```
                    ┌─────────────────┐
                    │   App Launch    │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │  Rating Screen  │◀──────────────┐
                    │    (Home)       │               │
                    └────────┬────────┘               │
                             │                        │
            ┌────────────────┼────────────────┐       │
            │                │                │       │
            ▼                ▼                ▼       │
    ┌───────────────┐ ┌───────────────┐ ┌───────────────┐
    │    History    │ │   Settings    │ │    Export     │
    │    Screen     │ │    Screen     │ │    Screen     │
    └───────┬───────┘ └───────┬───────┘ └───────────────┘
            │                 │
            ▼                 ├──────────────┐
    ┌───────────────┐         ▼              ▼
    │  Day Detail   │ ┌───────────────┐ ┌───────────────┐
    │    Screen     │ │ Family Setup  │ │   Custom      │
    └───────────────┘ │    Screen     │ │  Categories   │
                      └───────────────┘ └───────────────┘
```

---

## Route Definitions

```kotlin
@Serializable
sealed class Screen {
    
    /** Home screen - rate today's categories */
    @Serializable
    data object Rating : Screen()
    
    /** List of past days with ratings */
    @Serializable
    data object History : Screen()
    
    /** Detail view for a specific past day */
    @Serializable
    data class DayDetail(
        val date: String  // ISO-8601 format: "2026-01-15"
    ) : Screen()
    
    /** App settings */
    @Serializable
    data object Settings : Screen()
    
    /** Configure family members (spouse, children) */
    @Serializable
    data object FamilySetup : Screen()
    
    /** Manage custom rating categories */
    @Serializable
    data object CustomCategories : Screen()
    
    /** Export data screen */
    @Serializable
    data object Export : Screen()
}
```

---

## Screen Specifications

### Rating Screen (Home)

| Property | Value |
|----------|-------|
| Route | `Screen.Rating` |
| Start Destination | Yes |
| Back Navigation | Exit app |
| Bottom Nav | Yes (selected) |

**Content**:
- Today's date header
- List of active categories with emoji selectors
- Visual indication of rated vs unrated categories
- Navigation to History, Settings, Export via bottom bar or menu

---

### History Screen

| Property | Value |
|----------|-------|
| Route | `Screen.History` |
| Back Navigation | → Rating |
| Bottom Nav | Yes |

**Content**:
- List or calendar of days with ratings
- Each day shows date + summary (e.g., "5/7 rated, mostly 😊")
- Tap day → DayDetail

---

### Day Detail Screen

| Property | Value |
|----------|-------|
| Route | `Screen.DayDetail(date)` |
| Arguments | `date: String` (ISO-8601) |
| Back Navigation | → History |
| Bottom Nav | No |

**Content**:
- Date header
- Read-only list of all category ratings for that day
- Categories that weren't rated shown as "—" or "Not rated"

---

### Settings Screen

| Property | Value |
|----------|-------|
| Route | `Screen.Settings` |
| Back Navigation | → Rating |
| Bottom Nav | Yes |

**Content**:
- Theme selector (System/Light/Dark)
- Family Setup link
- Custom Categories link
- Export Data link
- About / Version info

---

### Family Setup Screen

| Property | Value |
|----------|-------|
| Route | `Screen.FamilySetup` |
| Back Navigation | → Settings |
| Bottom Nav | No |

**Content**:
- Spouse section: name input, add/remove
- Children section: list with add/rename/remove
- Real-time update of category labels

---

### Custom Categories Screen

| Property | Value |
|----------|-------|
| Route | `Screen.CustomCategories` |
| Back Navigation | → Settings |
| Bottom Nav | No |

**Content**:
- List of custom categories
- Add new category button
- Swipe or button to delete
- Tap to rename

---

### Export Screen

| Property | Value |
|----------|-------|
| Route | `Screen.Export` |
| Back Navigation | → Settings or Rating |
| Bottom Nav | No |

**Content**:
- Export format options (CSV, JSON)
- Date range selector (optional, default = all)
- Export button → Android Share Sheet
- Preview of export data (optional)

---

## Bottom Navigation Bar

```kotlin
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Rating, "Today", Icons.Default.Today),
    BottomNavItem(Screen.History, "History", Icons.Default.History),
    BottomNavItem(Screen.Settings, "Settings", Icons.Default.Settings)
)
```

---

## Deep Links (Optional Future)

Reserved for potential future use (e.g., notification opens specific screen):

| Pattern | Destination |
|---------|-------------|
| `dayrater://rate` | Rating |
| `dayrater://history` | History |
| `dayrater://day/{date}` | DayDetail |
