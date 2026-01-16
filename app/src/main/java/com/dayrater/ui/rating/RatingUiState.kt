package com.dayrater.ui.rating

import com.dayrater.data.local.entity.RatingValue
import com.dayrater.domain.model.Category
import com.dayrater.domain.model.FamilyMember
import java.time.LocalDate

/**
 * UI state for the rating screen.
 */
data class RatingUiState(
    val isLoading: Boolean = true,
    val date: LocalDate = LocalDate.now(),
    val familyMembers: List<FamilyMember> = emptyList(),
    val selectedFamilyMemberId: Long? = null,
    val categories: List<Category> = emptyList(),
    val ratings: Map<Long, RatingValue> = emptyMap(), // categoryId -> rating
    val error: String? = null
) {
    /**
     * The currently selected family member.
     */
    val selectedFamilyMember: FamilyMember?
        get() = familyMembers.find { it.id == selectedFamilyMemberId }
    
    /**
     * Whether today is being rated.
     */
    val isToday: Boolean
        get() = date == LocalDate.now()
    
    /**
     * Whether there are any ratings entered.
     */
    val hasRatings: Boolean
        get() = ratings.isNotEmpty()
    
    /**
     * Number of categories that have been rated.
     */
    val ratedCount: Int
        get() = ratings.size
    
    /**
     * Total number of categories.
     */
    val totalCategories: Int
        get() = categories.size
    
    /**
     * Progress as a fraction (0.0 to 1.0).
     */
    val progress: Float
        get() = if (totalCategories > 0) ratedCount.toFloat() / totalCategories else 0f
    
    /**
     * Get the rating for a specific category.
     */
    fun getRatingForCategory(categoryId: Long): RatingValue? = ratings[categoryId]
}

/**
 * Events that can be sent from the rating screen to the ViewModel.
 */
sealed interface RatingEvent {
    data class SelectFamilyMember(val familyMemberId: Long) : RatingEvent
    data class SetRating(val categoryId: Long, val rating: RatingValue) : RatingEvent
    data class ClearRating(val categoryId: Long) : RatingEvent
    data class ChangeDate(val date: LocalDate) : RatingEvent
    data object GoToToday : RatingEvent
    data object DismissError : RatingEvent
}
