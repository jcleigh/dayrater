package com.dayrater.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dayrater.data.local.entity.FamilyMemberEntity
import com.dayrater.data.local.entity.RelationshipType
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for family member operations.
 */
@Dao
interface FamilyMemberDao {
    
    /**
     * Observe all active family members.
     */
    @Query("SELECT * FROM family_members WHERE isActive = 1 ORDER BY displayOrder ASC")
    fun getActiveFamilyMembers(): Flow<List<FamilyMemberEntity>>
    
    /**
     * Observe all family members (including inactive).
     */
    @Query("SELECT * FROM family_members ORDER BY displayOrder ASC")
    fun getAllFamilyMembers(): Flow<List<FamilyMemberEntity>>
    
    /**
     * Get a family member by ID.
     */
    @Query("SELECT * FROM family_members WHERE id = :id")
    fun getById(id: Long): Flow<FamilyMemberEntity?>
    
    /**
     * Observe the self member.
     */
    @Query("SELECT * FROM family_members WHERE relationship = 'SELF' AND isActive = 1 LIMIT 1")
    fun getSelf(): Flow<FamilyMemberEntity?>
    
    /**
     * Observe the spouse (at most one).
     */
    @Query("SELECT * FROM family_members WHERE relationship = 'SPOUSE' AND isActive = 1 LIMIT 1")
    fun getSpouse(): Flow<FamilyMemberEntity?>
    
    /**
     * Observe all children.
     */
    @Query("SELECT * FROM family_members WHERE relationship = 'CHILD' AND isActive = 1 ORDER BY displayOrder ASC")
    fun getChildren(): Flow<List<FamilyMemberEntity>>
    
    /**
     * Insert a new family member.
     * @return The ID of the inserted family member
     */
    @Insert
    suspend fun insert(member: FamilyMemberEntity): Long
    
    /**
     * Update a family member's name.
     */
    @Query("UPDATE family_members SET name = :name WHERE id = :id")
    suspend fun updateName(id: Long, name: String)
    
    /**
     * Soft-delete a family member by setting isActive to false.
     */
    @Query("UPDATE family_members SET isActive = 0 WHERE id = :id")
    suspend fun softDelete(id: Long)
    
    /**
     * Restore a deleted family member.
     */
    @Query("UPDATE family_members SET isActive = 1 WHERE id = :id")
    suspend fun restore(id: Long)
    
    /**
     * Count family members by type.
     */
    @Query("SELECT COUNT(*) FROM family_members WHERE relationship = :type AND isActive = 1")
    suspend fun countByType(type: RelationshipType): Int
}
