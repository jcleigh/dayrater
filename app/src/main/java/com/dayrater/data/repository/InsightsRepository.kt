package com.dayrater.data.repository

import com.dayrater.domain.model.CategorySummary
import com.dayrater.domain.model.DayIndicator
import com.dayrater.domain.model.MonthlyCalendarData
import com.dayrater.domain.model.TrendData
import com.dayrater.domain.model.TrendTimeRange
import com.dayrater.domain.model.UserStatistics
import com.dayrater.domain.model.WeeklySummary
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

/**
 * Repository interface for insights and historical data operations.
 */
interface InsightsRepository {
    
    // ==================== Weekly Summary ====================
    
    /**
     * Observe weekly summary for a given week.
     * 
     * @param weekStart The first day of the week
     * @return Flow of WeeklySummary for that week
     */
    fun observeWeeklySummary(weekStart: LocalDate): Flow<WeeklySummary>
    
    /**
     * Get the daily breakdown for a category in a specific week.
     * 
     * @param categoryId The category to get breakdown for
     * @param weekStart The first day of the week
     * @return List of day indicators for each day of the week
     */
    fun getCategoryWeekBreakdown(categoryId: Long, weekStart: LocalDate): Flow<List<DayIndicator>>
    
    /**
     * Get the start of the week containing the given date.
     * Uses locale-aware first day of week (Sunday or Monday).
     * 
     * @param date Any date
     * @return The first day of that week
     */
    fun getWeekStart(date: LocalDate): LocalDate
    
    // ==================== Monthly Calendar ====================
    
    /**
     * Observe monthly calendar data for heat map display.
     * 
     * @param yearMonth The month to get data for
     * @return Flow of MonthlyCalendarData with day indicators
     */
    fun observeMonthlyCalendar(yearMonth: YearMonth): Flow<MonthlyCalendarData>
    
    // ==================== Trends ====================
    
    /**
     * Observe trend data for a category over a time range.
     * 
     * @param categoryId The category to get trends for
     * @param timeRange The time period to analyze
     * @return Flow of TrendData with data points
     */
    fun observeTrendData(categoryId: Long, timeRange: TrendTimeRange): Flow<TrendData>
    
    /**
     * Observe available categories for trend selection.
     * 
     * @return Flow of list of category ID/name pairs
     */
    fun observeTrendCategories(): Flow<List<Pair<Long, String>>>
    
    // ==================== Statistics ====================
    
    /**
     * Observe overall user statistics.
     * 
     * @return Flow of UserStatistics with streaks, averages, distribution
     */
    fun observeStatistics(): Flow<UserStatistics>
    
    /**
     * Check if user has minimum data for meaningful insights.
     * 
     * @return Flow of true if at least 4 days rated
     */
    fun hasMinimumDataForInsights(): Flow<Boolean>
}
