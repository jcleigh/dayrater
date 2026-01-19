package com.dayrater.ui.insights

/**
 * UI state for the insights hub screen.
 */
data class InsightsUiState(
    val hasEnoughData: Boolean = false,
    val isLoading: Boolean = true
)
