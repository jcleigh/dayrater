package com.dayrater.data.repository

import com.dayrater.data.local.entity.RatingValue
import com.dayrater.domain.model.Category
import com.dayrater.domain.model.DayRatings
import com.dayrater.domain.model.FamilyMember
import com.dayrater.domain.model.Rating
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for rating operations.
 * Provides a clean API for rating CRUD operations and category management.
 */
interface RatingRepository {
    
    // ==================== Rating Operations ====================
    
    /**
     * Get all ratings for a specific date and family member as a Flow.
     * 
     * @param date The date to get ratings for
     * @param familyMemberId The family member to get ratings for
     * @return Flow of DayRatings for the specified date and family member
     */
    fun getRatingsForDate(date: LocalDate, familyMemberId: Long): Flow<DayRatings?>
    
    /**
     * Get all ratings for a specific date for all family members.
     * 
     * @param date The date to get ratings for
     * @return Flow of map from family member ID to their DayRatings
     */
    fun getAllRatingsForDate(date: LocalDate): Flow<Map<Long, DayRatings>>
    
    /**
     * Save a rating for a specific category, date, and family member.
     * Updates existing rating if one exists, otherwise creates new.
     * 
     * @param categoryId The category being rated
     * @param familyMemberId The family member the rating is for
     * @param date The date of the rating
     * @param value The rating value
     */
    suspend fun saveRating(
        categoryId: Long,
        familyMemberId: Long,
        date: LocalDate,
        value: RatingValue
    )
    
    /**
     * Delete a specific rating.
     * 
     * @param categoryId The category of the rating
     * @param familyMemberId The family member of the rating
     * @param date The date of the rating
     */
    suspend fun deleteRating(categoryId: Long, familyMemberId: Long, date: LocalDate)
    
    /**
     * Get all dates that have at least one rating.
     * 
     * @return Flow of list of dates with ratings, sorted descending
     */
    fun getDatesWithRatings(): Flow<List<LocalDate>>
    
    /**
     * Get dates with ratings for a specific month.
     * 
     * @param year The year
     * @param month The month (1-12)
     * @return Flow of list of dates in that month with ratings
     */
    fun getDatesWithRatingsForMonth(year: Int, month: Int): Flow<List<LocalDate>>
    
    // ==================== Category Operations ====================
    
    /**
     * Get all active categories as a Flow.
     * 
     * @return Flow of list of active categories, sorted by display order
     */
    fun getActiveCategories(): Flow<List<Category>>
    
    /**
     * Get all categories (including inactive) as a Flow.
     * 
     * @return Flow of list of all categories, sorted by display order
     */
    fun getAllCategories(): Flow<List<Category>>
    
    /**
     * Add a new custom category.
     * 
     * @param name The name of the category
     * @return The ID of the created category
     */
    suspend fun addCategory(name: String): Long
    
    /**
     * Rename a category.
     * 
     * @param categoryId The ID of the category to rename
     * @param newName The new name
     */
    suspend fun renameCategory(categoryId: Long, newName: String)
    
    /**
     * Delete (soft delete) a category.
     * 
     * @param categoryId The ID of the category to delete
     */
    suspend fun deleteCategory(categoryId: Long)
    
    /**
     * Restore a deleted category.
     * 
     * @param categoryId The ID of the category to restore
     */
    suspend fun restoreCategory(categoryId: Long)
    
    // ==================== Export Operations ====================
    
    /**
     * Export all ratings to CSV format.
     * 
     * @param startDate Optional start date for export range
     * @param endDate Optional end date for export range
     * @return CSV string of all ratings
     */
    suspend fun exportToCsv(startDate: LocalDate? = null, endDate: LocalDate? = null): String
    
    /**
     * Export all ratings to JSON format.
     * 
     * @param startDate Optional start date for export range
     * @param endDate Optional end date for export range
     * @return JSON string of all ratings
     */
    suspend fun exportToJson(startDate: LocalDate? = null, endDate: LocalDate? = null): String
}
