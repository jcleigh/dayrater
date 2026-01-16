package com.dayrater.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a configured family member (spouse or child).
 * Family members have corresponding categories auto-created for rating interactions.
 */
@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /** Display name (e.g., "Alex", "Emma") */
    val name: String,
    
    /** SPOUSE or CHILD */
    val relationshipType: RelationshipType,
    
    /** Unix timestamp of creation */
    val createdAt: Long = System.currentTimeMillis()
)
