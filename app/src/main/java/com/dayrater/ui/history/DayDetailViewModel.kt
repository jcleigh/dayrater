package com.dayrater.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dayrater.data.local.entity.RatingValue
import com.dayrater.data.repository.FamilyRepository
import com.dayrater.data.repository.RatingRepository
import com.dayrater.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for the day detail screen.
 */
@HiltViewModel
class DayDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val ratingRepository: RatingRepository,
    private val familyRepository: FamilyRepository
) : ViewModel() {
    
    private val route = savedStateHandle.toRoute<Screen.History>()
    private val date = LocalDate.ofEpochDay(route.dateEpochDay)
    
    private val _uiState = MutableStateFlow(DayDetailUiState(date = date))
    val uiState: StateFlow<DayDetailUiState> = _uiState.asStateFlow()
    
    private var ratingsObservationJob: Job? = null
    
    init {
        loadData()
    }
    
    fun onEvent(event: DayDetailEvent) {
        when (event) {
            is DayDetailEvent.SelectFamilyMember -> selectFamilyMember(event.familyMemberId)
            is DayDetailEvent.ToggleEditMode -> toggleEditMode()
            is DayDetailEvent.SetRating -> setRating(event.categoryId, event.rating)
            is DayDetailEvent.DismissError -> dismissError()
        }
    }
    
    private fun loadData() {
        viewModelScope.launch {
            try {
                // Load family members first
                val familyMembers = familyRepository.getActiveFamilyMembers().first()
                val selfMember = familyMembers.find { it.isSelf }
                
                _uiState.update { state ->
                    state.copy(
                        familyMembers = familyMembers,
                        selectedFamilyMemberId = selfMember?.id ?: familyMembers.firstOrNull()?.id
                    )
                }
                
                // Then observe ratings
                observeRatings()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    private fun observeRatings() {
        // Cancel any existing observation before starting a new one
        ratingsObservationJob?.cancel()
        
        ratingsObservationJob = viewModelScope.launch {
            val currentState = _uiState.value
            val familyMemberId = currentState.selectedFamilyMemberId ?: return@launch
            
            combine(
                ratingRepository.getActiveCategories(),
                ratingRepository.getRatingsForDate(date, familyMemberId)
            ) { categories, dayRatings ->
                Pair(categories, dayRatings)
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
            .collect { (categories, dayRatings) ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        categories = categories,
                        dayRatings = dayRatings
                    )
                }
            }
        }
    }
    
    private fun selectFamilyMember(familyMemberId: Long) {
        _uiState.update { it.copy(selectedFamilyMemberId = familyMemberId, isLoading = true) }
        observeRatings()
    }
    
    private fun toggleEditMode() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }
    }
    
    private fun setRating(categoryId: Long, rating: RatingValue) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val familyMemberId = currentState.selectedFamilyMemberId ?: return@launch
                
                ratingRepository.saveRating(
                    categoryId = categoryId,
                    familyMemberId = familyMemberId,
                    date = date,
                    value = rating
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    private fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
