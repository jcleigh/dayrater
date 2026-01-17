package com.dayrater.domain.model

import com.dayrater.data.local.entity.RatingValue
import java.time.LocalDate

/**
 * Rating indicator for a single day in the calendar heat map.
 */
data class DayIndicator(
    val date: LocalDate,
    val overallRating: RatingValue?,  // null = no rating that day
    val ratingsCount: Int,            // How many categories rated
    val totalCategories: Int          // How many categories exist
) {
    val isComplete: Boolean
        get() = ratingsCount == totalCategories && totalCategories > 0
}
