package com.dayrater.domain.model

/**
 * Distribution of ratings across the three rating values.
 */
data class RatingDistribution(
    val positiveCount: Int,
    val neutralCount: Int,
    val negativeCount: Int
) {
    val total: Int
        get() = positiveCount + neutralCount + negativeCount
    
    val positivePercent: Float
        get() = if (total == 0) 0f else positiveCount.toFloat() / total * 100
    
    val neutralPercent: Float
        get() = if (total == 0) 0f else neutralCount.toFloat() / total * 100
    
    val negativePercent: Float
        get() = if (total == 0) 0f else negativeCount.toFloat() / total * 100
}
