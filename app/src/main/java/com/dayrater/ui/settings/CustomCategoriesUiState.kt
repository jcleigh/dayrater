package com.dayrater.ui.settings

import com.dayrater.domain.model.Category

/**
 * UI state for the custom categories screen.
 */
data class CustomCategoriesUiState(
    val isLoading: Boolean = true,
    val defaultCategories: List<Category> = emptyList(),
    val customCategories: List<Category> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingCategory: Category? = null,
    val deletingCategory: Category? = null,
    val error: String? = null
) {
    val totalCategories: Int
        get() = defaultCategories.size + customCategories.size
    
    val hasCustomCategories: Boolean
        get() = customCategories.isNotEmpty()
}

/**
 * Events that can be sent from the custom categories screen.
 */
sealed interface CustomCategoriesEvent {
    // Dialog controls
    data object ShowAddDialog : CustomCategoriesEvent
    data object DismissDialog : CustomCategoriesEvent
    
    // CRUD operations
    data class AddCategory(val name: String) : CustomCategoriesEvent
    data class EditCategory(val category: Category) : CustomCategoriesEvent
    data class RenameCategory(val categoryId: Long, val name: String) : CustomCategoriesEvent
    data class ConfirmDelete(val category: Category) : CustomCategoriesEvent
    data class DeleteCategory(val categoryId: Long) : CustomCategoriesEvent
    data object CancelDelete : CustomCategoriesEvent
    
    // Error handling
    data object DismissError : CustomCategoriesEvent
}
