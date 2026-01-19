package com.dayrater.ui.insights

import com.dayrater.domain.model.UserStatistics

/**
 * UI state for the statistics screen.
 */
data class StatisticsUiState(
    val isLoading: Boolean = true,
    val statistics: UserStatistics? = null,
    val error: String? = null
)
