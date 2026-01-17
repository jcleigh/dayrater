package com.dayrater.domain.model

import java.time.LocalDate

/**
 * Aggregated data for a 7-day period.
 */
data class WeeklySummary(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val categorySummaries: List<CategorySummary>,
    val daysRatedCount: Int,
    val totalDays: Int = 7
) {
    val completionPercent: Float
        get() = daysRatedCount.toFloat() / totalDays
}
