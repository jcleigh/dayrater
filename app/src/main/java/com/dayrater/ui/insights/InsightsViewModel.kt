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
 * ViewModel for the insights hub screen.
 */
@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val insightsRepository: InsightsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()
    
    init {
        checkDataAvailability()
    }
    
    private fun checkDataAvailability() {
        viewModelScope.launch {
            insightsRepository.hasMinimumDataForInsights().collect { hasData ->
                _uiState.update {
                    it.copy(
                        hasEnoughData = hasData,
                        isLoading = false
                    )
                }
            }
        }
    }
}
