package com.dayrater.domain.model

import com.dayrater.data.local.entity.RatingValue

/**
 * Aggregation for one category over a time period.
 */
data class CategorySummary(
    val categoryId: Long,
    val categoryName: String,
    val positiveCount: Int,
    val neutralCount: Int,
    val negativeCount: Int
) {
    val totalRatings: Int
        get() = positiveCount + neutralCount + negativeCount
    
    /** Average as numeric: NEGATIVE=1, NEUTRAL=2, POSITIVE=3 */
    val averageScore: Float?
        get() = if (totalRatings == 0) null else {
            (negativeCount * 1 + neutralCount * 2 + positiveCount * 3).toFloat() / totalRatings
        }
    
    /** Dominant rating (most frequent) */
    val dominantRating: RatingValue?
        get() = when {
            totalRatings == 0 -> null
            positiveCount >= neutralCount && positiveCount >= negativeCount -> RatingValue.POSITIVE
            neutralCount >= negativeCount -> RatingValue.NEUTRAL
            else -> RatingValue.NEGATIVE
        }
}
