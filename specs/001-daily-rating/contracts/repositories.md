# Repository Contracts: Daily Rating Core

**Feature**: 001-daily-rating  
**Date**: 2026-01-16

This document defines the repository interfaces (contracts) that the UI layer depends on. These are internal APIs—no REST/GraphQL since DayRater is a local-only app.

---

## RatingRepository

Manages daily ratings and category data.

```kotlin
interface RatingRepository {
    
    // === Today's Ratings ===
    
    /**
     * Observe today's ratings for all active categories.
     * Emits a new value whenever ratings or categories change.
     */
    fun observeTodayRatings(): Flow<DayRatings>
    
    /**
     * Save or update a rating for a category on today's date.
     * If a rating already exists for this category today, it is replaced.
     */
    suspend fun saveRating(categoryId: Long, value: RatingValue)
    
    // === History ===
    
    /**
     * Observe a summary of all days that have at least one rating.
     * Returns dates in descending order (most recent first).
     */
    fun observeRatingHistory(): Flow<List<LocalDate>>
    
    /**
     * Get all ratings for a specific date.
     * Returns null if no ratings exist for that date.
     */
    suspend fun getRatingsForDate(date: LocalDate): DayRatings?
    
    // === Categories ===
    
    /**
     * Observe all active categories in display order.
     */
    fun observeActiveCategories(): Flow<List<Category>>
    
    /**
     * Create a new custom category.
     * @return The ID of the created category
     */
    suspend fun createCustomCategory(name: String): Long
    
    /**
     * Soft-delete a category (sets isActive = false).
     * Historical ratings are preserved.
     */
    suspend fun deleteCategory(categoryId: Long)
    
    /**
     * Rename a category.
     */
    suspend fun renameCategory(categoryId: Long, newName: String)
    
    // === Export ===
    
    /**
     * Export all rating data as CSV string.
     * Format: date,category,category_type,rating,rating_label
     */
    suspend fun exportToCsv(): String
    
    /**
     * Export all rating data as JSON string.
     */
    suspend fun exportToJson(): String
}
```

---

## FamilyRepository

Manages family member configuration.

```kotlin
interface FamilyRepository {
    
    /**
     * Observe all family members.
     */
    fun observeFamilyMembers(): Flow<List<FamilyMember>>
    
    /**
     * Get the configured spouse, if any.
     */
    fun observeSpouse(): Flow<FamilyMember?>
    
    /**
     * Get all configured children.
     */
    fun observeChildren(): Flow<List<FamilyMember>>
    
    /**
     * Add or update the spouse.
     * If a spouse already exists, updates their name.
     * Also creates/updates the SPOUSE category.
     * @return The ID of the spouse
     */
    suspend fun setSpouse(name: String): Long
    
    /**
     * Remove the spouse configuration.
     * Soft-deletes the associated SPOUSE category.
     */
    suspend fun removeSpouse()
    
    /**
     * Add a new child.
     * Also creates a CHILD category for them.
     * @return The ID of the child
     */
    suspend fun addChild(name: String): Long
    
    /**
     * Rename a child.
     * Also updates the associated CHILD category name.
     */
    suspend fun renameChild(childId: Long, newName: String)
    
    /**
     * Remove a child.
     * Soft-deletes the associated CHILD category.
     */
    suspend fun removeChild(childId: Long)
}
```

---

## SettingsRepository

Manages app preferences (non-rating data).

```kotlin
interface SettingsRepository {
    
    /**
     * Observe the current theme preference.
     */
    fun observeThemePreference(): Flow<ThemePreference>
    
    /**
     * Set the theme preference.
     */
    suspend fun setThemePreference(theme: ThemePreference)
    
    /**
     * Check if this is the first launch (for onboarding).
     */
    suspend fun isFirstLaunch(): Boolean
    
    /**
     * Mark first launch as complete.
     */
    suspend fun completeFirstLaunch()
}

enum class ThemePreference {
    SYSTEM, // Follow system setting
    LIGHT,
    DARK
}
```

---

## DAO Contracts (Room)

Lower-level data access for repositories.

### RatingDao

```kotlin
@Dao
interface RatingDao {
    
    @Query("SELECT * FROM daily_ratings WHERE date = :date")
    fun observeRatingsForDate(date: LocalDate): Flow<List<DailyRatingEntity>>
    
    @Query("SELECT * FROM daily_ratings WHERE date = :date AND categoryId = :categoryId")
    suspend fun getRating(date: LocalDate, categoryId: Long): DailyRatingEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRating(rating: DailyRatingEntity)
    
    @Query("SELECT DISTINCT date FROM daily_ratings ORDER BY date DESC")
    fun observeAllDatesWithRatings(): Flow<List<LocalDate>>
    
    @Query("SELECT * FROM daily_ratings ORDER BY date DESC, categoryId ASC")
    suspend fun getAllRatings(): List<DailyRatingEntity>
}
```

### CategoryDao

```kotlin
@Dao
interface CategoryDao {
    
    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY displayOrder ASC")
    fun observeActiveCategories(): Flow<List<CategoryEntity>>
    
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): CategoryEntity?
    
    @Query("SELECT * FROM categories WHERE familyMemberId = :familyMemberId")
    suspend fun getCategoryByFamilyMember(familyMemberId: Long): CategoryEntity?
    
    @Insert
    suspend fun insertCategory(category: CategoryEntity): Long
    
    @Query("UPDATE categories SET isActive = 0 WHERE id = :id")
    suspend fun softDeleteCategory(id: Long)
    
    @Query("UPDATE categories SET name = :name WHERE id = :id")
    suspend fun renameCategory(id: Long, name: String)
    
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryEntity>
}
```

### FamilyMemberDao

```kotlin
@Dao
interface FamilyMemberDao {
    
    @Query("SELECT * FROM family_members")
    fun observeAllFamilyMembers(): Flow<List<FamilyMemberEntity>>
    
    @Query("SELECT * FROM family_members WHERE relationshipType = 'SPOUSE' LIMIT 1")
    fun observeSpouse(): Flow<FamilyMemberEntity?>
    
    @Query("SELECT * FROM family_members WHERE relationshipType = 'CHILD'")
    fun observeChildren(): Flow<List<FamilyMemberEntity>>
    
    @Insert
    suspend fun insertFamilyMember(member: FamilyMemberEntity): Long
    
    @Query("UPDATE family_members SET name = :name WHERE id = :id")
    suspend fun renameFamilyMember(id: Long, name: String)
    
    @Query("DELETE FROM family_members WHERE id = :id")
    suspend fun deleteFamilyMember(id: Long)
    
    @Query("DELETE FROM family_members WHERE relationshipType = 'SPOUSE'")
    suspend fun deleteSpouse()
}
```

---

## Data Flow Summary

```
┌──────────────┐     ┌──────────────────┐     ┌─────────────┐
│   Compose    │────▶│   ViewModel      │────▶│  Repository │
│     UI       │◀────│  (StateFlow)     │◀────│   (Flow)    │
└──────────────┘     └──────────────────┘     └──────┬──────┘
                                                     │
                                                     ▼
                                              ┌─────────────┐
                                              │  Room DAO   │
                                              │  (SQLite)   │
                                              └─────────────┘
```

All repository methods returning `Flow<T>` are observable and automatically update the UI when data changes.
