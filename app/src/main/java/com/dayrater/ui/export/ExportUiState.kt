package com.dayrater.ui.export

import java.time.LocalDate

/**
 * UI state for the export screen.
 */
data class ExportUiState(
    val isLoading: Boolean = false,
    val selectedDateRange: DateRange = DateRange.ALL_TIME,
    val customStartDate: LocalDate? = null,
    val customEndDate: LocalDate? = null,
    val exportFormat: ExportFormat = ExportFormat.CSV,
    val isExporting: Boolean = false,
    val exportSuccess: Boolean = false,
    val exportedFilePath: String? = null,
    val error: String? = null
) {
    /**
     * Whether custom date selection is active.
     */
    val isCustomRange: Boolean
        get() = selectedDateRange == DateRange.CUSTOM
    
    /**
     * Get the effective start date based on selection.
     */
    val effectiveStartDate: LocalDate?
        get() = when (selectedDateRange) {
            DateRange.ALL_TIME -> null
            DateRange.LAST_30_DAYS -> LocalDate.now().minusDays(30)
            DateRange.LAST_90_DAYS -> LocalDate.now().minusDays(90)
            DateRange.CUSTOM -> customStartDate
        }
    
    /**
     * Get the effective end date based on selection.
     */
    val effectiveEndDate: LocalDate?
        get() = when (selectedDateRange) {
            DateRange.ALL_TIME -> null
            DateRange.LAST_30_DAYS, DateRange.LAST_90_DAYS -> LocalDate.now()
            DateRange.CUSTOM -> customEndDate
        }
}

/**
 * Date range options for export.
 */
enum class DateRange {
    ALL_TIME,
    LAST_30_DAYS,
    LAST_90_DAYS,
    CUSTOM
}

/**
 * Export format options.
 */
enum class ExportFormat {
    CSV,
    JSON
}

/**
 * Events that can be sent from the export screen.
 */
sealed interface ExportEvent {
    data class SelectDateRange(val range: DateRange) : ExportEvent
    data class SetCustomStartDate(val date: LocalDate) : ExportEvent
    data class SetCustomEndDate(val date: LocalDate) : ExportEvent
    data class SelectFormat(val format: ExportFormat) : ExportEvent
    data object Export : ExportEvent
    data object Share : ExportEvent
    data object DismissSuccess : ExportEvent
    data object DismissError : ExportEvent
}
