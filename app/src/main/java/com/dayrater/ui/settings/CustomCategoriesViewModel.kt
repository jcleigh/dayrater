package com.dayrater.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayrater.data.local.entity.CategoryType
import com.dayrater.data.repository.RatingRepository
import com.dayrater.domain.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the custom categories screen.
 */
@HiltViewModel
class CustomCategoriesViewModel @Inject constructor(
    private val ratingRepository: RatingRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CustomCategoriesUiState())
    val uiState: StateFlow<CustomCategoriesUiState> = _uiState.asStateFlow()
    
    init {
        loadCategories()
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            ratingRepository.getActiveCategories()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { categories ->
                    val defaultCategories = categories.filter { it.type == CategoryType.DEFAULT }
                    val customCategories = categories.filter { it.type == CategoryType.CUSTOM }
                    
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            defaultCategories = defaultCategories,
                            customCategories = customCategories
                        )
                    }
                }
        }
    }
    
    fun onEvent(event: CustomCategoriesEvent) {
        when (event) {
            is CustomCategoriesEvent.ShowAddDialog -> {
                _uiState.update { it.copy(showAddDialog = true) }
            }
            is CustomCategoriesEvent.DismissDialog -> {
                _uiState.update { it.copy(
                    showAddDialog = false,
                    editingCategory = null
                ) }
            }
            is CustomCategoriesEvent.AddCategory -> addCategory(event.name)
            is CustomCategoriesEvent.EditCategory -> {
                _uiState.update { it.copy(editingCategory = event.category) }
            }
            is CustomCategoriesEvent.RenameCategory -> renameCategory(event.categoryId, event.name)
            is CustomCategoriesEvent.ConfirmDelete -> {
                _uiState.update { it.copy(deletingCategory = event.category) }
            }
            is CustomCategoriesEvent.DeleteCategory -> deleteCategory(event.categoryId)
            is CustomCategoriesEvent.CancelDelete -> {
                _uiState.update { it.copy(deletingCategory = null) }
            }
            is CustomCategoriesEvent.DismissError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }
    
    private fun addCategory(name: String) {
        if (name.isBlank()) {
            _uiState.update { it.copy(error = "Name cannot be empty") }
            return
        }
        
        // Check for duplicates
        val allCategories = _uiState.value.defaultCategories + _uiState.value.customCategories
        if (allCategories.any { it.name.equals(name.trim(), ignoreCase = true) }) {
            _uiState.update { it.copy(error = "A category with this name already exists") }
            return
        }
        
        viewModelScope.launch {
            try {
                ratingRepository.addCategory(name.trim())
                _uiState.update { it.copy(showAddDialog = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    private fun renameCategory(categoryId: Long, name: String) {
        if (name.isBlank()) {
            _uiState.update { it.copy(error = "Name cannot be empty") }
            return
        }
        
        // Check for duplicates (excluding the current category)
        val allCategories = _uiState.value.defaultCategories + _uiState.value.customCategories
        if (allCategories.any { it.id != categoryId && it.name.equals(name.trim(), ignoreCase = true) }) {
            _uiState.update { it.copy(error = "A category with this name already exists") }
            return
        }
        
        viewModelScope.launch {
            try {
                ratingRepository.renameCategory(categoryId, name.trim())
                _uiState.update { it.copy(editingCategory = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    private fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            try {
                ratingRepository.deleteCategory(categoryId)
                _uiState.update { it.copy(deletingCategory = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
