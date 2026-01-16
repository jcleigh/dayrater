package com.dayrater.domain.model

import java.time.LocalDate

/**
 * Domain model representing all ratings for a specific day and family member.
 * 
 * @property date The date these ratings are for
 * @property familyMember The family member these ratings are for
 * @property ratings List of ratings by category
 */
data class DayRatings(
    val date: LocalDate,
    val familyMember: FamilyMember,
    val ratings: List<Rating>
) {
    /**
     * Whether any ratings have been entered for this day.
     */
    val hasRatings: Boolean
        get() = ratings.isNotEmpty()
    
    /**
     * Number of categories that have been rated.
     */
    val ratedCount: Int
        get() = ratings.size
    
    /**
     * Get the rating for a specific category, if it exists.
     */
    fun getRatingForCategory(categoryId: Long): Rating? {
        return ratings.find { it.categoryId == categoryId }
    }
    
    /**
     * Whether this day is today.
     */
    val isToday: Boolean
        get() = date == LocalDate.now()
    
    /**
     * Whether this day is in the past.
     */
    val isPast: Boolean
        get() = date.isBefore(LocalDate.now())
    
    /**
     * Whether this day is in the future.
     */
    val isFuture: Boolean
        get() = date.isAfter(LocalDate.now())
}
