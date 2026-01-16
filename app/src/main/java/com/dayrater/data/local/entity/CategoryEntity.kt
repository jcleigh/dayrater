package com.dayrater.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a ratable category.
 * Categories can be default (built-in), family-member-based (spouse/child), or custom.
 */
@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = FamilyMemberEntity::class,
            parentColumns = ["id"],
            childColumns = ["familyMemberId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["familyMemberId"]),
        Index(value = ["isActive", "displayOrder"])
    ]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /** Display name for the category (e.g., "Overall Day", "Interactions with Alex") */
    val name: String,
    
    /** Type of category: DEFAULT, SPOUSE, CHILD, or CUSTOM */
    val type: CategoryType,
    
    /** Foreign key to FamilyMember for SPOUSE/CHILD types; null for DEFAULT/CUSTOM */
    val familyMemberId: Long? = null,
    
    /** Order for UI display; lower values appear first */
    val displayOrder: Int,
    
    /** Soft delete flag; false = hidden but data preserved */
    val isActive: Boolean = true,
    
    /** Unix timestamp of creation */
    val createdAt: Long = System.currentTimeMillis()
)
