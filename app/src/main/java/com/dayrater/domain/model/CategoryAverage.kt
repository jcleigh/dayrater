package com.dayrater.domain.model

/**
 * Average rating for a single category.
 */
data class CategoryAverage(
    val categoryId: Long,
    val categoryName: String,
    val averageScore: Float,      // 1.0 - 3.0
    val totalRatings: Int
)
