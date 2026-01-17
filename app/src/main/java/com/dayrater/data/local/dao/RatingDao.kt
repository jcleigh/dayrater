package com.dayrater.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dayrater.data.local.entity.DailyRatingEntity
import com.dayrater.data.local.entity.RatingValue
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Helper data classes for query results.
 */
data class RatingWithCategory(
    val id: Long,
    val date: LocalDate,
    val categoryId: Long?,
    val categoryName: String?,
    val ratingValue: RatingValue,
    val updatedAt: Long
)

data class DateRatingPair(
    val date: LocalDate,
    val ratingValue: RatingValue
)

data class RatingCount(
    val ratingValue: RatingValue,
    val count: Int
)

/**
 * Data Access Object for daily rating operations.
 */
@Dao
interface RatingDao {
    
    /**
     * Observe all ratings for a specific date.
     */
    @Query("SELECT * FROM daily_ratings WHERE date = :date ORDER BY categoryId")
    fun observeRatingsForDate(date: LocalDate): Flow<List<DailyRatingEntity>>
    
    /**
     * Get ratings for a specific date and family member (reactive).
     */
    @Query("SELECT * FROM daily_ratings WHERE date = :date AND familyMemberId = :familyMemberId ORDER BY categoryId")
    fun getRatingsForDate(date: LocalDate, familyMemberId: Long): Flow<List<DailyRatingEntity>>
    
    /**
     * Get all ratings for a specific date (reactive).
     */
    @Query("SELECT * FROM daily_ratings WHERE date = :date ORDER BY familyMemberId, categoryId")
    fun getAllRatingsForDate(date: LocalDate): Flow<List<DailyRatingEntity>>
    
    /**
     * Get a specific rating for a date and category.
     */
    @Query("SELECT * FROM daily_ratings WHERE date = :date AND categoryId = :categoryId")
    suspend fun getRating(date: LocalDate, categoryId: Long): DailyRatingEntity?
    
    /**
     * Upsert a rating (insert or replace).
     * Uses the unique index on (date, familyMemberId, categoryId) to handle conflicts.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRating(rating: DailyRatingEntity)
    
    /**
     * Delete a specific rating.
     */
    @Query("DELETE FROM daily_ratings WHERE familyMemberId = :familyMemberId AND categoryId = :categoryId AND date = :date")
    suspend fun deleteRating(familyMemberId: Long, categoryId: Long, date: LocalDate)
    
    /**
     * Observe all distinct dates that have at least one rating, in descending order.
     */
    @Query("SELECT DISTINCT date FROM daily_ratings ORDER BY date DESC")
    fun getAllDatesWithRatings(): Flow<List<LocalDate>>
    
    /**
     * Get dates with ratings in a specific range (for calendar view).
     */
    @Query("SELECT DISTINCT date FROM daily_ratings WHERE date >= :startDate AND date <= :endDate ORDER BY date")
    fun getDatesWithRatingsInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<LocalDate>>
    
    /**
     * Get all ratings in a date range (for export).
     */
    @Query("SELECT * FROM daily_ratings WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC, familyMemberId, categoryId")
    suspend fun getRatingsInRange(startDate: LocalDate, endDate: LocalDate): List<DailyRatingEntity>
    
    /**
     * Get all ratings for export.
     */
    @Query("SELECT * FROM daily_ratings ORDER BY date DESC, familyMemberId, categoryId")
    suspend fun getAllRatings(): List<DailyRatingEntity>
    
    /**
     * Count ratings for a specific date.
     */
    @Query("SELECT COUNT(*) FROM daily_ratings WHERE date = :date")
    suspend fun countRatingsForDate(date: LocalDate): Int
    
    /**
     * Delete all ratings (for testing or data reset).
     */
    @Query("DELETE FROM daily_ratings")
    suspend fun deleteAllRatings()
    
    // ========== INSIGHTS QUERIES ==========
    
    /**
     * Observe all ratings within a date range, with category info.
     * Used for weekly/monthly summaries.
     */
    @Query("""
        SELECT dr.id, dr.date, dr.categoryId, c.name as categoryName, dr.ratingValue, dr.updatedAt
        FROM daily_ratings dr
        LEFT JOIN categories c ON dr.categoryId = c.id
        WHERE dr.date BETWEEN :startDate AND :endDate
        ORDER BY dr.date ASC, c.displayOrder ASC
    """)
    fun observeRatingsInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<RatingWithCategory>>
    
    /**
     * Observe all distinct dates that have at least one rating, for streak calculation.
     */
    @Query("SELECT DISTINCT date FROM daily_ratings ORDER BY date DESC")
    fun observeAllRatedDates(): Flow<List<LocalDate>>
    
    /**
     * Observe rating history for a specific category.
     * Used for trend graphs.
     */
    @Query("""
        SELECT date, ratingValue FROM daily_ratings
        WHERE categoryId = :categoryId
        ORDER BY date ASC
    """)
    fun observeCategoryHistory(categoryId: Long): Flow<List<DateRatingPair>>
    
    /**
     * Observe ratings for "Overall Day" category in a date range.
     * Used for monthly calendar heat map.
     */
    @Query("""
        SELECT dr.date, dr.ratingValue FROM daily_ratings dr
        INNER JOIN categories c ON dr.categoryId = c.id
        WHERE c.name = 'Overall Day' 
        AND dr.date BETWEEN :startDate AND :endDate
    """)
    fun observeOverallRatingsInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DateRatingPair>>
    
    /**
     * Observe count of ratings grouped by rating value.
     * Used for distribution statistics.
     */
    @Query("""
        SELECT ratingValue, COUNT(*) as count
        FROM daily_ratings
        GROUP BY ratingValue
    """)
    fun observeRatingDistribution(): Flow<List<RatingCount>>
    
    /**
     * Observe total number of distinct days with at least one rating.
     */
    @Query("SELECT COUNT(DISTINCT date) FROM daily_ratings")
    fun observeTotalRatedDays(): Flow<Int>
}
