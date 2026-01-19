package com.dayrater.ui.insights

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayrater.data.repository.InsightsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for the weekly summary screen.
 */
@HiltViewModel
class WeeklySummaryViewModel @Inject constructor(
    private val insightsRepository: InsightsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WeeklySummaryUiState())
    val uiState: StateFlow<WeeklySummaryUiState> = _uiState.asStateFlow()
    
    init {
        // Get initial week start from nav arg or use current week
        val weekStartEpochDay = savedStateHandle.get<Long>("weekStartEpochDay")
        val initialWeekStart = if (weekStartEpochDay != null) {
            LocalDate.ofEpochDay(weekStartEpochDay)
        } else {
            insightsRepository.getWeekStart(LocalDate.now())
        }
        
        _uiState.update { it.copy(weekStart = initialWeekStart) }
        loadWeeklySummary(initialWeekStart)
    }
    
    private fun loadWeeklySummary(weekStart: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            insightsRepository.observeWeeklySummary(weekStart).collect { summary ->
                val today = LocalDate.now()
                val currentWeekStart = insightsRepository.getWeekStart(today)
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        summary = summary,
                        canGoNext = weekStart < currentWeekStart
                    )
                }
            }
        }
    }
    
    fun goToPreviousWeek() {
        val newWeekStart = _uiState.value.weekStart.minusWeeks(1)
        _uiState.update { it.copy(weekStart = newWeekStart) }
        loadWeeklySummary(newWeekStart)
    }
    
    fun goToNextWeek() {
        val newWeekStart = _uiState.value.weekStart.plusWeeks(1)
        val today = LocalDate.now()
        val currentWeekStart = insightsRepository.getWeekStart(today)
        
        if (newWeekStart <= currentWeekStart) {
            _uiState.update { it.copy(weekStart = newWeekStart) }
            loadWeeklySummary(newWeekStart)
        }
    }
}
