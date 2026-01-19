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
import javax.inject.Inject

/**
 * ViewModel for the statistics screen.
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val insightsRepository: InsightsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()
    
    init {
        loadStatistics()
    }
    
    private fun loadStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            insightsRepository.observeStatistics().collect { statistics ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        statistics = statistics
                    )
                }
            }
        }
    }
}
