package com.dayrater.ui.insights

import com.dayrater.domain.model.DayIndicator
import java.time.LocalDate

/**
 * UI state for the category week detail screen.
 */
data class CategoryWeekDetailUiState(
    val isLoading: Boolean = true,
    val categoryName: String = "",
    val weekStart: LocalDate = LocalDate.now(),
    val dailyBreakdown: List<DayIndicator> = emptyList(),
    val error: String? = null
)
