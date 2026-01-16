package com.dayrater.domain.model

import com.dayrater.data.local.entity.RelationshipType

/**
 * Domain model representing a family member.
 * 
 * @property id Unique identifier
 * @property name Display name of the family member
 * @property relationship Type of relationship (SPOUSE, CHILD, SELF)
 * @property displayOrder Order in which to display this family member
 * @property isActive Whether this family member is currently active
 */
data class FamilyMember(
    val id: Long = 0,
    val name: String,
    val relationship: RelationshipType,
    val displayOrder: Int,
    val isActive: Boolean = true
) {
    /**
     * Whether this family member is the user themselves.
     */
    val isSelf: Boolean
        get() = relationship == RelationshipType.SELF
    
    /**
     * Whether this family member is the spouse.
     */
    val isSpouse: Boolean
        get() = relationship == RelationshipType.SPOUSE
    
    /**
     * Whether this family member is a child.
     */
    val isChild: Boolean
        get() = relationship == RelationshipType.CHILD
    
    /**
     * Whether this family member can be deleted (self cannot be deleted).
     */
    val canDelete: Boolean
        get() = !isSelf
}
