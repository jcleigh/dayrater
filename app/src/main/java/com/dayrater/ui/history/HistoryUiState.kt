package com.dayrater.ui.history

import java.time.LocalDate
import java.time.YearMonth

/**
 * UI state for the history/calendar screen.
 */
data class HistoryUiState(
    val isLoading: Boolean = true,
    val currentMonth: YearMonth = YearMonth.now(),
    val datesWithRatings: Set<LocalDate> = emptySet(),
    val selectedDate: LocalDate? = null,
    val error: String? = null
) {
    /**
     * Get all days in the current month.
     */
    val daysInMonth: List<LocalDate>
        get() {
            val firstDay = currentMonth.atDay(1)
            val lastDay = currentMonth.atEndOfMonth()
            return (1..lastDay.dayOfMonth).map { firstDay.withDayOfMonth(it) }
        }
    
    /**
     * First day of the week for the current month (for padding calendar grid).
     */
    val firstDayOfWeekOffset: Int
        get() = currentMonth.atDay(1).dayOfWeek.value % 7 // 0 = Sunday
    
    /**
     * Whether we can navigate to the next month (not in the future).
     */
    val canGoToNextMonth: Boolean
        get() = currentMonth.isBefore(YearMonth.now())
    
    /**
     * Check if a date has ratings.
     */
    fun hasRatingsForDate(date: LocalDate): Boolean = date in datesWithRatings
    
    /**
     * Check if a date is today.
     */
    fun isToday(date: LocalDate): Boolean = date == LocalDate.now()
    
    /**
     * Check if a date is in the future.
     */
    fun isFuture(date: LocalDate): Boolean = date.isAfter(LocalDate.now())
}

/**
 * Events that can be sent from the history screen.
 */
sealed interface HistoryEvent {
    data object PreviousMonth : HistoryEvent
    data object NextMonth : HistoryEvent
    data class SelectDate(val date: LocalDate) : HistoryEvent
    data object GoToToday : HistoryEvent
    data object DismissError : HistoryEvent
}
