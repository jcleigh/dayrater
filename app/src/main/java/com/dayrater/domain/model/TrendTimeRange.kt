package com.dayrater.domain.model

/**
 * Available time ranges for trend graphs.
 */
enum class TrendTimeRange(val days: Int, val label: String) {
    WEEK(7, "1 Week"),
    MONTH(30, "1 Month"),
    THREE_MONTHS(90, "3 Months"),
    YEAR(365, "1 Year"),
    ALL_TIME(-1, "All Time")  // -1 means no limit
}
