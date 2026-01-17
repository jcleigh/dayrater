package com.dayrater.domain.model

import java.time.DayOfWeek
import java.time.YearMonth

/**
 * A month's worth of daily rating indicators for heat map display.
 */
data class MonthlyCalendarData(
    val yearMonth: YearMonth,
    val dayRatings: Map<Int, DayIndicator>,
    val firstDayOfWeek: DayOfWeek
) {
    val daysInMonth: Int
        get() = yearMonth.lengthOfMonth()
    
    /** Offset for first day placement in grid (0 = starts on first weekday) */
    val startOffset: Int
        get() {
            val firstOfMonth = yearMonth.atDay(1).dayOfWeek
            return (firstOfMonth.value - firstDayOfWeek.value + 7) % 7
        }
}
