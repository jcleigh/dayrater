package com.dayrater.ui.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayrater.data.local.entity.RatingValue
import com.dayrater.data.repository.FamilyRepository
import com.dayrater.data.repository.RatingRepository
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
 * ViewModel for the rating screen.
 * Manages state for rating today's (or any day's) categories.
 */
@HiltViewModel
class RatingViewModel @Inject constructor(
    private val ratingRepository: RatingRepository,
    private val familyRepository: FamilyRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RatingUiState())
    val uiState: StateFlow<RatingUiState> = _uiState.asStateFlow()
    
    private var ratingsObservationJob: Job? = null
    
    init {
        loadInitialData()
    }
    
    /**
     * Handle UI events from the rating screen.
     */
    fun onEvent(event: RatingEvent) {
        when (event) {
            is RatingEvent.SelectFamilyMember -> selectFamilyMember(event.familyMemberId)
            is RatingEvent.SetRating -> setRating(event.categoryId, event.rating)
            is RatingEvent.ClearRating -> clearRating(event.categoryId)
            is RatingEvent.ChangeDate -> changeDate(event.date)
            is RatingEvent.GoToToday -> changeDate(LocalDate.now())
            is RatingEvent.DismissError -> dismissError()
        }
    }
    
    private fun loadInitialData() {
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
                
                // Then observe categories and ratings
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
            val date = currentState.date
            
            combine(
                ratingRepository.getActiveCategories(),
                ratingRepository.getRatingsForDate(date, familyMemberId)
            ) { categories, dayRatings ->
                val ratingsMap = dayRatings?.ratings?.associate { 
                    it.categoryId to it.value 
                } ?: emptyMap()
                
                Pair(categories, ratingsMap)
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
            .collect { (categories, ratingsMap) ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        categories = categories,
                        ratings = ratingsMap,
                        error = null
                    )
                }
            }
        }
    }
    
    private fun selectFamilyMember(familyMemberId: Long) {
        _uiState.update { it.copy(selectedFamilyMemberId = familyMemberId, isLoading = true) }
        observeRatings()
    }
    
    private fun setRating(categoryId: Long, rating: RatingValue) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val familyMemberId = currentState.selectedFamilyMemberId ?: return@launch
                
                // Optimistically update UI
                _uiState.update { state ->
                    state.copy(ratings = state.ratings + (categoryId to rating))
                }
                
                // Persist to database
                ratingRepository.saveRating(
                    categoryId = categoryId,
                    familyMemberId = familyMemberId,
                    date = currentState.date,
                    value = rating
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    private fun clearRating(categoryId: Long) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val familyMemberId = currentState.selectedFamilyMemberId ?: return@launch
                
                // Optimistically update UI
                _uiState.update { state ->
                    state.copy(ratings = state.ratings - categoryId)
                }
                
                // Delete from database
                ratingRepository.deleteRating(
                    categoryId = categoryId,
                    familyMemberId = familyMemberId,
                    date = currentState.date
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    private fun changeDate(date: LocalDate) {
        _uiState.update { it.copy(date = date, isLoading = true) }
        observeRatings()
    }
    
    private fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
