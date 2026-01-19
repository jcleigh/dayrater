package com.dayrater.ui.insights

import com.dayrater.domain.model.MonthlyCalendarData
import java.time.YearMonth

/**
 * UI state for the monthly calendar screen.
 */
data class MonthlyCalendarUiState(
    val isLoading: Boolean = true,
    val yearMonth: YearMonth = YearMonth.now(),
    val calendarData: MonthlyCalendarData? = null,
    val canGoNext: Boolean = false,
    val error: String? = null
)
