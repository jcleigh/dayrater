package com.dayrater.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dayrater.data.local.entity.CategoryEntity
import com.dayrater.data.local.entity.CategoryType
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for category operations.
 */
@Dao
interface CategoryDao {
    
    /**
     * Observe all active categories ordered by display order.
     */
    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY displayOrder ASC")
    fun getActiveCategories(): Flow<List<CategoryEntity>>
    
    /**
     * Observe all categories (including inactive) ordered by display order.
     */
    @Query("SELECT * FROM categories ORDER BY displayOrder ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>
    
    /**
     * Get a category by ID.
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): CategoryEntity?
    
    /**
     * Get a category by family member ID.
     */
    @Query("SELECT * FROM categories WHERE familyMemberId = :familyMemberId AND isActive = 1")
    suspend fun getCategoryByFamilyMember(familyMemberId: Long): CategoryEntity?
    
    /**
     * Insert a new category.
     * @return The ID of the inserted category
     */
    @Insert
    suspend fun insert(category: CategoryEntity): Long
    
    /**
     * Soft-delete a category by setting isActive to false.
     */
    @Query("UPDATE categories SET isActive = 0 WHERE id = :id")
    suspend fun softDelete(id: Long)
    
    /**
     * Restore a deleted category.
     */
    @Query("UPDATE categories SET isActive = 1 WHERE id = :id")
    suspend fun restore(id: Long)
    
    /**
     * Rename a category.
     */
    @Query("UPDATE categories SET name = :name WHERE id = :id")
    suspend fun renameCategory(id: Long, name: String)
    
    /**
     * Get the maximum display order for determining new category order.
     */
    @Query("SELECT MAX(displayOrder) FROM categories")
    suspend fun getMaxDisplayOrder(): Int?
    
    /**
     * Check if default categories have been seeded.
     */
    @Query("SELECT COUNT(*) FROM categories WHERE type = :type")
    suspend fun countCategoriesByType(type: CategoryType): Int
}
