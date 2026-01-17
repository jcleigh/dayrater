package com.dayrater.ui.insights

import com.dayrater.domain.model.WeeklySummary
import java.time.LocalDate

/**
 * UI state for the weekly summary screen.
 */
data class WeeklySummaryUiState(
    val isLoading: Boolean = true,
    val weekStart: LocalDate = LocalDate.now(),
    val summary: WeeklySummary? = null,
    val canGoNext: Boolean = false,
    val error: String? = null
)
