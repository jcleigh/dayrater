package com.dayrater.domain.model

import com.dayrater.data.local.entity.RatingValue
import java.time.LocalDate

/**
 * A single point on a trend graph.
 */
data class TrendDataPoint(
    val date: LocalDate,
    val value: Float?,           // 1.0, 2.0, or 3.0 (or null)
    val ratingValue: RatingValue? // Original enum for display
) {
    companion object {
        fun fromRating(date: LocalDate, rating: RatingValue?): TrendDataPoint {
            val numericValue = when (rating) {
                RatingValue.NEGATIVE -> 1f
                RatingValue.NEUTRAL -> 2f
                RatingValue.POSITIVE -> 3f
                null -> null
            }
            return TrendDataPoint(date, numericValue, rating)
        }
    }
}
