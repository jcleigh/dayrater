package com.dayrater.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayrater.data.repository.RatingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * ViewModel for the history/calendar screen.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val ratingRepository: RatingRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    
    init {
        loadDatesWithRatings()
    }
    
    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.PreviousMonth -> navigateMonth(-1)
            is HistoryEvent.NextMonth -> navigateMonth(1)
            is HistoryEvent.SelectDate -> selectDate(event.date)
            is HistoryEvent.GoToToday -> goToToday()
            is HistoryEvent.DismissError -> dismissError()
        }
    }
    
    private fun loadDatesWithRatings() {
        val currentMonth = _uiState.value.currentMonth
        
        viewModelScope.launch {
            ratingRepository.getDatesWithRatingsForMonth(currentMonth.year, currentMonth.monthValue)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { dates ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            datesWithRatings = dates.toSet()
                        )
                    }
                }
        }
    }
    
    private fun navigateMonth(delta: Int) {
        val newMonth = _uiState.value.currentMonth.plusMonths(delta.toLong())
        
        // Don't allow navigating to future months
        if (newMonth.isAfter(YearMonth.now())) return
        
        _uiState.update { it.copy(currentMonth = newMonth, isLoading = true) }
        loadDatesWithRatings()
    }
    
    private fun selectDate(date: LocalDate) {
        // Don't allow selecting future dates
        if (date.isAfter(LocalDate.now())) return
        
        _uiState.update { it.copy(selectedDate = date) }
    }
    
    private fun goToToday() {
        val today = LocalDate.now()
        val currentMonth = YearMonth.from(today)
        
        _uiState.update { it.copy(
            currentMonth = currentMonth,
            selectedDate = today,
            isLoading = true
        ) }
        loadDatesWithRatings()
    }
    
    private fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
