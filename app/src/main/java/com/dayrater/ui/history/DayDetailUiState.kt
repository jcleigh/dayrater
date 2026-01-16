package com.dayrater.ui.history

import com.dayrater.domain.model.Category
import com.dayrater.domain.model.DayRatings
import com.dayrater.domain.model.FamilyMember
import java.time.LocalDate

/**
 * UI state for the day detail screen.
 */
data class DayDetailUiState(
    val isLoading: Boolean = true,
    val date: LocalDate = LocalDate.now(),
    val familyMembers: List<FamilyMember> = emptyList(),
    val selectedFamilyMemberId: Long? = null,
    val categories: List<Category> = emptyList(),
    val dayRatings: DayRatings? = null,
    val isEditing: Boolean = false,
    val error: String? = null
) {
    val selectedFamilyMember: FamilyMember?
        get() = familyMembers.find { it.id == selectedFamilyMemberId }
    
    val isToday: Boolean
        get() = date == LocalDate.now()
    
    val hasRatings: Boolean
        get() = dayRatings?.hasRatings == true
}

/**
 * Events that can be sent from the day detail screen.
 */
sealed interface DayDetailEvent {
    data class SelectFamilyMember(val familyMemberId: Long) : DayDetailEvent
    data object ToggleEditMode : DayDetailEvent
    data class SetRating(val categoryId: Long, val rating: com.dayrater.data.local.entity.RatingValue) : DayDetailEvent
    data object DismissError : DayDetailEvent
}
