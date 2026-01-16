package com.dayrater.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayrater.data.repository.FamilyRepository
import com.dayrater.data.repository.RatingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the settings screen.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val ratingRepository: RatingRepository,
    private val familyRepository: FamilyRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            combine(
                ratingRepository.getActiveCategories(),
                familyRepository.getActiveFamilyMembers()
            ) { categories, familyMembers ->
                Pair(categories.size, familyMembers.size)
            }
            .catch { e ->
                _uiState.update { it.copy(error = e.message) }
            }
            .collect { (categoryCount, familyMemberCount) ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        categoryCount = categoryCount,
                        familyMemberCount = familyMemberCount
                    )
                }
            }
        }
    }
    
    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.DismissError -> {
                _uiState.update { it.copy(error = null) }
            }
            // Navigation events are handled by the composable
            else -> {}
        }
    }
}
