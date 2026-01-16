package com.dayrater.data.repository

import com.dayrater.data.local.entity.RelationshipType
import com.dayrater.domain.model.FamilyMember
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for family member operations.
 */
interface FamilyRepository {
    
    /**
     * Get all active family members as a Flow.
     * 
     * @return Flow of list of active family members, sorted by display order
     */
    fun getActiveFamilyMembers(): Flow<List<FamilyMember>>
    
    /**
     * Get all family members (including inactive) as a Flow.
     * 
     * @return Flow of list of all family members
     */
    fun getAllFamilyMembers(): Flow<List<FamilyMember>>
    
    /**
     * Get the self member (should always exist).
     * 
     * @return Flow of the self FamilyMember
     */
    fun getSelf(): Flow<FamilyMember?>
    
    /**
     * Get the spouse member if one exists.
     * 
     * @return Flow of the spouse FamilyMember, or null
     */
    fun getSpouse(): Flow<FamilyMember?>
    
    /**
     * Get all children.
     * 
     * @return Flow of list of children
     */
    fun getChildren(): Flow<List<FamilyMember>>
    
    /**
     * Add a new family member.
     * 
     * @param name The name of the family member
     * @param relationship The relationship type
     * @return The ID of the created family member
     */
    suspend fun addFamilyMember(name: String, relationship: RelationshipType): Long
    
    /**
     * Update a family member's name.
     * 
     * @param memberId The ID of the family member
     * @param name The new name
     */
    suspend fun updateName(memberId: Long, name: String)
    
    /**
     * Delete (soft delete) a family member.
     * 
     * @param memberId The ID of the family member to delete
     */
    suspend fun deleteFamilyMember(memberId: Long)
    
    /**
     * Restore a deleted family member.
     * 
     * @param memberId The ID of the family member to restore
     */
    suspend fun restoreFamilyMember(memberId: Long)
    
    /**
     * Check if a spouse already exists.
     * 
     * @return True if a spouse exists
     */
    suspend fun hasSpouse(): Boolean
    
    /**
     * Initialize the self member if it doesn't exist.
     * Called on first app launch.
     */
    suspend fun initializeSelf()
}
