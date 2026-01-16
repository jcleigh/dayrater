package com.dayrater.domain.model

import com.dayrater.data.local.entity.CategoryType

/**
 * Domain model representing a rating category.
 * 
 * @property id Unique identifier
 * @property name Display name of the category
 * @property type Whether this is a default or custom category
 * @property displayOrder Order in which to display this category
 * @property isActive Whether this category is currently active
 */
data class Category(
    val id: Long = 0,
    val name: String,
    val type: CategoryType,
    val displayOrder: Int,
    val isActive: Boolean = true
) {
    /**
     * Whether this category can be deleted (only custom categories can be deleted).
     */
    val canDelete: Boolean
        get() = type == CategoryType.CUSTOM
    
    /**
     * Whether this category can be renamed.
     */
    val canRename: Boolean
        get() = true
}
