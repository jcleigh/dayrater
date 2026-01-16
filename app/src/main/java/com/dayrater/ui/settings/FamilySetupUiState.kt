package com.dayrater.ui.settings

import com.dayrater.domain.model.FamilyMember

/**
 * UI state for the family setup screen.
 */
data class FamilySetupUiState(
    val isLoading: Boolean = true,
    val selfMember: FamilyMember? = null,
    val spouse: FamilyMember? = null,
    val children: List<FamilyMember> = emptyList(),
    val showAddSpouseDialog: Boolean = false,
    val showAddChildDialog: Boolean = false,
    val editingMember: FamilyMember? = null,
    val deletingMember: FamilyMember? = null,
    val error: String? = null
) {
    val hasSpouse: Boolean
        get() = spouse != null
    
    val childCount: Int
        get() = children.size
    
    val totalMembers: Int
        get() = 1 + (if (hasSpouse) 1 else 0) + childCount
}

/**
 * Events that can be sent from the family setup screen.
 */
sealed interface FamilySetupEvent {
    // Dialog controls
    data object ShowAddSpouseDialog : FamilySetupEvent
    data object ShowAddChildDialog : FamilySetupEvent
    data object DismissDialog : FamilySetupEvent
    
    // CRUD operations
    data class AddSpouse(val name: String) : FamilySetupEvent
    data class AddChild(val name: String) : FamilySetupEvent
    data class EditMember(val member: FamilyMember) : FamilySetupEvent
    data class UpdateMemberName(val memberId: Long, val name: String) : FamilySetupEvent
    data class ConfirmDelete(val member: FamilyMember) : FamilySetupEvent
    data class DeleteMember(val memberId: Long) : FamilySetupEvent
    data object CancelDelete : FamilySetupEvent
    
    // Self update
    data class UpdateSelfName(val name: String) : FamilySetupEvent
    
    // Error handling
    data object DismissError : FamilySetupEvent
}
