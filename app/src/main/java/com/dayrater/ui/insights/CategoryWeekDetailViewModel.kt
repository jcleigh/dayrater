package com.dayrater.ui.insights

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayrater.data.local.dao.CategoryDao
import com.dayrater.data.repository.InsightsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for the category week detail screen.
 */
@HiltViewModel
class CategoryWeekDetailViewModel @Inject constructor(
    private val insightsRepository: InsightsRepository,
    private val categoryDao: CategoryDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val categoryId: Long = savedStateHandle.get<Long>("categoryId") ?: 0L
    private val weekStartEpochDay: Long = savedStateHandle.get<Long>("weekStartEpochDay") ?: LocalDate.now().toEpochDay()
    
    private val _uiState = MutableStateFlow(CategoryWeekDetailUiState())
    val uiState: StateFlow<CategoryWeekDetailUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val weekStart = LocalDate.ofEpochDay(weekStartEpochDay)
            
            // Get category name
            val category = categoryDao.getCategoryById(categoryId)
            val categoryName = category?.name ?: "Unknown"
            
            _uiState.update {
                it.copy(
                    weekStart = weekStart,
                    categoryName = categoryName
                )
            }
            
            // Observe daily breakdown
            insightsRepository.getCategoryWeekBreakdown(categoryId, weekStart).collect { breakdown ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        dailyBreakdown = breakdown
                    )
                }
            }
        }
    }
}
