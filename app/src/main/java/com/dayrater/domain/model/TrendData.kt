package com.dayrater.domain.model

/**
 * Collection of data points for a trend graph with metadata.
 */
data class TrendData(
    val categoryId: Long,
    val categoryName: String,
    val timeRange: TrendTimeRange,
    val dataPoints: List<TrendDataPoint>
) {
    val averageScore: Float?
        get() {
            val values = dataPoints.mapNotNull { it.value }
            return if (values.isEmpty()) null else values.average().toFloat()
        }
    
    val ratedDaysCount: Int
        get() = dataPoints.count { it.value != null }
}
