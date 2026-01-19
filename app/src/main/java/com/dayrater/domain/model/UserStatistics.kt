package com.dayrater.domain.model

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Computed statistics across all rating history.
 */
data class UserStatistics(
    val currentStreak: Int,
    val longestStreak: Int,
    val totalDaysRated: Int,
    val firstRatingDate: LocalDate?,
    val categoryAverages: Map<Long, CategoryAverage>,
    val dayOfWeekAverages: Map<DayOfWeek, Float>,
    val overallDistribution: RatingDistribution
)
