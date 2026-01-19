package com.dayrater.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayrater.data.repository.InsightsRepository
import com.dayrater.domain.model.TrendTimeRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the trends screen.
 */
@HiltViewModel
class TrendsViewModel @Inject constructor(
    private val insightsRepository: InsightsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TrendsUiState())
    val uiState: StateFlow<TrendsUiState> = _uiState.asStateFlow()
    
    private var trendDataJob: Job? = null
    
    init {
        loadCategories()
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            insightsRepository.observeTrendCategories().collect { categories ->
                _uiState.update { state ->
                    val selectedId = state.selectedCategoryId ?: categories.firstOrNull()?.first
                    state.copy(
                        availableCategories = categories,
                        selectedCategoryId = selectedId
                    )
                }
                
                // Load trend data for selected category
                _uiState.value.selectedCategoryId?.let { categoryId ->
                    loadTrendData(categoryId, _uiState.value.selectedTimeRange)
                }
            }
        }
    }
    
    private fun loadTrendData(categoryId: Long, timeRange: TrendTimeRange) {
        trendDataJob?.cancel()
        trendDataJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            insightsRepository.observeTrendData(categoryId, timeRange).collect { trendData ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        trendData = trendData
                    )
                }
            }
        }
    }
    
    fun selectCategory(categoryId: Long) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
        loadTrendData(categoryId, _uiState.value.selectedTimeRange)
    }
    
    fun selectTimeRange(timeRange: TrendTimeRange) {
        _uiState.update { it.copy(selectedTimeRange = timeRange) }
        _uiState.value.selectedCategoryId?.let { categoryId ->
            loadTrendData(categoryId, timeRange)
        }
    }
}
