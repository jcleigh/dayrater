package com.dayrater.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayrater.data.repository.InsightsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

/**
 * ViewModel for the monthly calendar screen.
 */
@HiltViewModel
class MonthlyCalendarViewModel @Inject constructor(
    private val insightsRepository: InsightsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MonthlyCalendarUiState())
    val uiState: StateFlow<MonthlyCalendarUiState> = _uiState.asStateFlow()
    
    init {
        loadMonthData(YearMonth.now())
    }
    
    private fun loadMonthData(yearMonth: YearMonth) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            insightsRepository.observeMonthlyCalendar(yearMonth).collect { calendarData ->
                val currentMonth = YearMonth.now()
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        yearMonth = yearMonth,
                        calendarData = calendarData,
                        canGoNext = yearMonth < currentMonth
                    )
                }
            }
        }
    }
    
    fun goToPreviousMonth() {
        val newMonth = _uiState.value.yearMonth.minusMonths(1)
        _uiState.update { it.copy(yearMonth = newMonth) }
        loadMonthData(newMonth)
    }
    
    fun goToNextMonth() {
        val newMonth = _uiState.value.yearMonth.plusMonths(1)
        val currentMonth = YearMonth.now()
        
        if (newMonth <= currentMonth) {
            _uiState.update { it.copy(yearMonth = newMonth) }
            loadMonthData(newMonth)
        }
    }
}
