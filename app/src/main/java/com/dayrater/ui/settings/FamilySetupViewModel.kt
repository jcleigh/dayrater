package com.dayrater.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayrater.data.local.entity.RelationshipType
import com.dayrater.data.repository.FamilyRepository
import com.dayrater.domain.model.FamilyMember
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
 * ViewModel for the family setup screen.
 */
@HiltViewModel
class FamilySetupViewModel @Inject constructor(
    private val familyRepository: FamilyRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FamilySetupUiState())
    val uiState: StateFlow<FamilySetupUiState> = _uiState.asStateFlow()
    
    init {
        loadFamilyMembers()
    }
    
    private fun loadFamilyMembers() {
        viewModelScope.launch {
            combine(
                familyRepository.getSelf(),
                familyRepository.getSpouse(),
                familyRepository.getChildren()
            ) { self, spouse, children ->
                Triple(self, spouse, children)
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
            .collect { (self, spouse, children) ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        selfMember = self,
                        spouse = spouse,
                        children = children
                    )
                }
            }
        }
    }
    
    fun onEvent(event: FamilySetupEvent) {
        when (event) {
            is FamilySetupEvent.ShowAddSpouseDialog -> {
                _uiState.update { it.copy(showAddSpouseDialog = true) }
            }
            is FamilySetupEvent.ShowAddChildDialog -> {
                _uiState.update { it.copy(showAddChildDialog = true) }
            }
            is FamilySetupEvent.DismissDialog -> {
                _uiState.update { it.copy(
                    showAddSpouseDialog = false,
                    showAddChildDialog = false,
                    editingMember = null
                ) }
            }
            is FamilySetupEvent.AddSpouse -> addSpouse(event.name)
            is FamilySetupEvent.AddChild -> addChild(event.name)
            is FamilySetupEvent.EditMember -> {
                _uiState.update { it.copy(editingMember = event.member) }
            }
            is FamilySetupEvent.UpdateMemberName -> updateMemberName(event.memberId, event.name)
            is FamilySetupEvent.ConfirmDelete -> {
                _uiState.update { it.copy(deletingMember = event.member) }
            }
            is FamilySetupEvent.DeleteMember -> deleteMember(event.memberId)
            is FamilySetupEvent.CancelDelete -> {
                _uiState.update { it.copy(deletingMember = null) }
            }
            is FamilySetupEvent.UpdateSelfName -> updateSelfName(event.name)
            is FamilySetupEvent.DismissError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }
    
    private fun addSpouse(name: String) {
        if (name.isBlank()) {
            _uiState.update { it.copy(error = "Name cannot be empty") }
            return
        }
        
        viewModelScope.launch {
            try {
                if (familyRepository.hasSpouse()) {
                    _uiState.update { it.copy(error = "You can only have one spouse") }
                    return@launch
                }
                
                familyRepository.addFamilyMember(name.trim(), RelationshipType.SPOUSE)
                _uiState.update { it.copy(showAddSpouseDialog = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    private fun addChild(name: String) {
        if (name.isBlank()) {
            _uiState.update { it.copy(error = "Name cannot be empty") }
            return
        }
        
        viewModelScope.launch {
            try {
                familyRepository.addFamilyMember(name.trim(), RelationshipType.CHILD)
                _uiState.update { it.copy(showAddChildDialog = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    private fun updateMemberName(memberId: Long, name: String) {
        if (name.isBlank()) {
            _uiState.update { it.copy(error = "Name cannot be empty") }
            return
        }
        
        viewModelScope.launch {
            try {
                familyRepository.updateName(memberId, name.trim())
                _uiState.update { it.copy(editingMember = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    private fun updateSelfName(name: String) {
        val selfId = _uiState.value.selfMember?.id ?: return
        updateMemberName(selfId, name)
    }
    
    private fun deleteMember(memberId: Long) {
        viewModelScope.launch {
            try {
                familyRepository.deleteFamilyMember(memberId)
                _uiState.update { it.copy(deletingMember = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
