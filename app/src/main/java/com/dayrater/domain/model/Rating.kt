package com.dayrater.domain.model

import com.dayrater.data.local.entity.RatingValue

/**
 * Domain model representing a single rating for a category on a specific day.
 * 
 * @property categoryId ID of the category being rated
 * @property categoryName Name of the category (for display)
 * @property value The emoji rating value
 */
data class Rating(
    val categoryId: Long,
    val categoryName: String,
    val value: RatingValue
) {
    /**
     * Display emoji for this rating.
     */
    val emoji: String
        get() = value.emoji
    
    /**
     * Display label for this rating.
     */
    val label: String
        get() = value.label
}
