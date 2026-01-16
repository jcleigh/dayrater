package com.dayrater.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayrater.data.repository.FamilyRepository
import com.dayrater.data.repository.RatingRepository
import com.dayrater.data.repository.SettingsRepository
import com.dayrater.data.repository.ThemeMode
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
    private val familyRepository: FamilyRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            try {
                // Use separate collections to avoid combine issues
                launch {
                    ratingRepository.getActiveCategories()
                        .catch { e -> 
                            _uiState.update { it.copy(error = "Failed to load categories: ${e.message}") }
                        }
                        .collect { categories ->
                            _uiState.update { it.copy(categoryCount = categories.size, isLoading = false) }
                        }
                }
                
                launch {
                    familyRepository.getActiveFamilyMembers()
                        .catch { e ->
                            _uiState.update { it.copy(error = "Failed to load family members: ${e.message}") }
                        }
                        .collect { familyMembers ->
                            _uiState.update { it.copy(familyMemberCount = familyMembers.size) }
                        }
                }
                
                launch {
                    settingsRepository.themeMode
                        .catch { e ->
                            _uiState.update { it.copy(error = "Failed to load theme: ${e.message}") }
                        }
                        .collect { themeMode ->
                            _uiState.update { it.copy(themeMode = themeMode) }
                        }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }
    
    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.DismissError -> {
                _uiState.update { it.copy(error = null) }
            }
            is SettingsEvent.ShowThemeDialog -> {
                _uiState.update { it.copy(showThemeDialog = true) }
            }
            is SettingsEvent.DismissThemeDialog -> {
                _uiState.update { it.copy(showThemeDialog = false) }
            }
            is SettingsEvent.SetThemeMode -> {
                viewModelScope.launch {
                    settingsRepository.setThemeMode(event.mode)
                    _uiState.update { it.copy(showThemeDialog = false) }
                }
            }
            // Navigation events are handled by the composable
            else -> {}
        }
    }
}
