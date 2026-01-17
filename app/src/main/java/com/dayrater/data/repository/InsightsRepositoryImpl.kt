package com.dayrater.data.repository

import com.dayrater.data.local.dao.CategoryDao
import com.dayrater.data.local.dao.RatingDao
import com.dayrater.data.local.dao.RatingWithCategory
import com.dayrater.data.local.entity.RatingValue
import com.dayrater.domain.model.CategoryAverage
import com.dayrater.domain.model.CategorySummary
import com.dayrater.domain.model.DayIndicator
import com.dayrater.domain.model.MonthlyCalendarData
import com.dayrater.domain.model.RatingDistribution
import com.dayrater.domain.model.TrendData
import com.dayrater.domain.model.TrendDataPoint
import com.dayrater.domain.model.TrendTimeRange
import com.dayrater.domain.model.UserStatistics
import com.dayrater.domain.model.WeeklySummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of InsightsRepository.
 * Computes aggregated insights from raw rating data.
 */
@Singleton
class InsightsRepositoryImpl @Inject constructor(
    private val ratingDao: RatingDao,
    private val categoryDao: CategoryDao
) : InsightsRepository {
    
    private val weekFields = WeekFields.of(Locale.getDefault())
    
    // ==================== Weekly Summary ====================
    
    override fun observeWeeklySummary(weekStart: LocalDate): Flow<WeeklySummary> {
        val weekEnd = weekStart.plusDays(6)
        return ratingDao.observeRatingsInRange(weekStart, weekEnd).map { ratings ->
            buildWeeklySummary(weekStart, weekEnd, ratings)
        }
    }
    
    private fun buildWeeklySummary(
        weekStart: LocalDate,
        weekEnd: LocalDate,
        ratings: List<RatingWithCategory>
    ): WeeklySummary {
        // Group by category
        val byCategory = ratings.groupBy { it.categoryId to it.categoryName }
        val categorySummaries = byCategory.mapNotNull { (key, categoryRatings) ->
            val (categoryId, categoryName) = key
            if (categoryId == null || categoryName == null) return@mapNotNull null
            CategorySummary(
                categoryId = categoryId,
                categoryName = categoryName,
                positiveCount = categoryRatings.count { it.ratingValue == RatingValue.POSITIVE },
                neutralCount = categoryRatings.count { it.ratingValue == RatingValue.NEUTRAL },
                negativeCount = categoryRatings.count { it.ratingValue == RatingValue.NEGATIVE }
            )
        }
        
        // Count distinct days rated
        val daysRated = ratings.map { it.date }.distinct().size
        
        return WeeklySummary(
            startDate = weekStart,
            endDate = weekEnd,
            categorySummaries = categorySummaries,
            daysRatedCount = daysRated
        )
    }
    
    override fun getCategoryWeekBreakdown(categoryId: Long, weekStart: LocalDate): Flow<List<DayIndicator>> {
        val weekEnd = weekStart.plusDays(6)
        return combine(
            ratingDao.observeRatingsInRange(weekStart, weekEnd),
            categoryDao.observeActiveCategories()
        ) { ratings, categories ->
            val totalCategories = categories.size
            val categoryRatings = ratings.filter { it.categoryId == categoryId }
            
            // Build indicator for each day of the week
            (0..6).map { dayOffset ->
                val date = weekStart.plusDays(dayOffset.toLong())
                val dayRating = categoryRatings.find { it.date == date }
                val allDayRatings = ratings.filter { it.date == date }
                DayIndicator(
                    date = date,
                    overallRating = dayRating?.ratingValue,
                    ratingsCount = allDayRatings.size,
                    totalCategories = totalCategories
                )
            }
        }
    }
    
    override fun getWeekStart(date: LocalDate): LocalDate {
        val firstDayOfWeek = weekFields.firstDayOfWeek
        return date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    }
    
    // ==================== Monthly Calendar ====================
    
    override fun observeMonthlyCalendar(yearMonth: YearMonth): Flow<MonthlyCalendarData> {
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        
        return combine(
            ratingDao.observeOverallRatingsInRange(startDate, endDate),
            ratingDao.observeRatingsInRange(startDate, endDate),
            categoryDao.observeActiveCategories()
        ) { overallRatings, allRatings, categories ->
            val totalCategories = categories.size
            val overallByDate = overallRatings.associateBy { it.date }
            val ratingCountByDate = allRatings.groupBy { it.date }.mapValues { it.value.size }
            
            val dayRatings = (1..yearMonth.lengthOfMonth()).associateWith { day ->
                val date = yearMonth.atDay(day)
                DayIndicator(
                    date = date,
                    overallRating = overallByDate[date]?.ratingValue,
                    ratingsCount = ratingCountByDate[date] ?: 0,
                    totalCategories = totalCategories
                )
            }
            
            MonthlyCalendarData(
                yearMonth = yearMonth,
                dayRatings = dayRatings,
                firstDayOfWeek = weekFields.firstDayOfWeek
            )
        }
    }
    
    // ==================== Trends ====================
    
    override fun observeTrendData(categoryId: Long, timeRange: TrendTimeRange): Flow<TrendData> {
        return combine(
            ratingDao.observeCategoryHistory(categoryId),
            categoryDao.observeCategoryById(categoryId)
        ) { history, category ->
            val categoryName = category?.name ?: "Unknown"
            val endDate = LocalDate.now()
            val startDate = if (timeRange.days > 0) {
                endDate.minusDays(timeRange.days.toLong())
            } else {
                history.minOfOrNull { it.date } ?: endDate
            }
            
            // Filter to time range and convert to data points
            val filteredHistory = history.filter { 
                it.date in startDate..endDate 
            }
            val dataPoints = filteredHistory.map { pair ->
                TrendDataPoint.fromRating(pair.date, pair.ratingValue)
            }
            
            TrendData(
                categoryId = categoryId,
                categoryName = categoryName,
                timeRange = timeRange,
                dataPoints = dataPoints
            )
        }
    }
    
    override fun observeTrendCategories(): Flow<List<Pair<Long, String>>> {
        return categoryDao.observeActiveCategories().map { categories ->
            categories.map { it.id to it.name }
        }
    }
    
    // ==================== Statistics ====================
    
    override fun observeStatistics(): Flow<UserStatistics> {
        return combine(
            ratingDao.observeAllRatedDates(),
            ratingDao.observeRatingDistribution(),
            ratingDao.observeRatingsInRange(
                LocalDate.now().minusYears(10), // Far past
                LocalDate.now()
            ),
            categoryDao.observeActiveCategories()
        ) { dates, distribution, allRatings, categories ->
            val (currentStreak, longestStreak) = calculateStreaks(dates)
            val distMap = distribution.associate { it.ratingValue to it.count }
            
            UserStatistics(
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                totalDaysRated = dates.size,
                firstRatingDate = dates.minOrNull(),
                categoryAverages = calculateCategoryAverages(allRatings, categories.associate { it.id to it.name }),
                dayOfWeekAverages = calculateDayOfWeekAverages(allRatings),
                overallDistribution = RatingDistribution(
                    positiveCount = distMap[RatingValue.POSITIVE] ?: 0,
                    neutralCount = distMap[RatingValue.NEUTRAL] ?: 0,
                    negativeCount = distMap[RatingValue.NEGATIVE] ?: 0
                )
            )
        }
    }
    
    override fun hasMinimumDataForInsights(): Flow<Boolean> {
        return ratingDao.observeTotalRatedDays().map { it >= 4 }
    }
    
    // ==================== Helper Functions ====================
    
    /**
     * Calculate current and longest streaks from rated dates.
     */
    fun calculateStreaks(ratedDates: List<LocalDate>): Pair<Int, Int> {
        if (ratedDates.isEmpty()) return 0 to 0
        
        val sortedDates = ratedDates.sortedDescending()
        val today = LocalDate.now()
        
        // Current streak: count consecutive days from today backward
        var currentStreak = 0
        var expectedDate = today
        for (date in sortedDates) {
            if (date == expectedDate) {
                currentStreak++
                expectedDate = expectedDate.minusDays(1)
            } else if (date < expectedDate) {
                break
            }
        }
        
        // Longest streak: find max consecutive run
        var longestStreak = 0
        var runLength = 1
        for (i in 0 until sortedDates.size - 1) {
            if (sortedDates[i].minusDays(1) == sortedDates[i + 1]) {
                runLength++
            } else {
                longestStreak = maxOf(longestStreak, runLength)
                runLength = 1
            }
        }
        longestStreak = maxOf(longestStreak, runLength)
        
        return currentStreak to longestStreak
    }
    
    /**
     * Calculate average rating for each category.
     */
    private fun calculateCategoryAverages(
        ratings: List<RatingWithCategory>,
        categoryNames: Map<Long, String>
    ): Map<Long, CategoryAverage> {
        return ratings
            .filter { it.categoryId != null }
            .groupBy { it.categoryId!! }
            .mapValues { (categoryId, categoryRatings) ->
                val avgScore = categoryRatings.map { it.ratingValue.toNumeric() }.average().toFloat()
                CategoryAverage(
                    categoryId = categoryId,
                    categoryName = categoryNames[categoryId] ?: "Unknown",
                    averageScore = avgScore,
                    totalRatings = categoryRatings.size
                )
            }
    }
    
    /**
     * Calculate average rating per day of week.
     */
    fun calculateDayOfWeekAverages(ratings: List<RatingWithCategory>): Map<DayOfWeek, Float> {
        return ratings
            .groupBy { it.date.dayOfWeek }
            .mapValues { (_, dayRatings) ->
                dayRatings.map { it.ratingValue.toNumeric() }.average().toFloat()
            }
    }
    
    private fun RatingValue.toNumeric(): Float = when (this) {
        RatingValue.NEGATIVE -> 1f
        RatingValue.NEUTRAL -> 2f
        RatingValue.POSITIVE -> 3f
    }
}
