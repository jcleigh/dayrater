package com.dayrater.ui.insights

import com.dayrater.domain.model.TrendData
import com.dayrater.domain.model.TrendTimeRange

/**
 * UI state for the trends screen.
 */
data class TrendsUiState(
    val isLoading: Boolean = true,
    val selectedCategoryId: Long? = null,
    val selectedTimeRange: TrendTimeRange = TrendTimeRange.MONTH,
    val availableCategories: List<Pair<Long, String>> = emptyList(),
    val trendData: TrendData? = null,
    val error: String? = null
)
