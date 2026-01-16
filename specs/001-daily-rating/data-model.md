# Data Model: Daily Rating Core

**Feature**: 001-daily-rating  
**Date**: 2026-01-16  
**Source**: [spec.md](spec.md) Key Entities + [research.md](research.md) Room Design

## Entity Relationship Diagram

```
┌─────────────────────┐       ┌─────────────────────┐
│   FamilyMember      │       │      Category       │
├─────────────────────┤       ├─────────────────────┤
│ PK id: Long         │──┐    │ PK id: Long         │
│    name: String     │  │    │    name: String     │
│    relationshipType │  └───▶│ FK familyMemberId?  │
│    createdAt: Long  │       │    type: CategoryType│
└─────────────────────┘       │    displayOrder: Int │
                              │    isActive: Boolean │
                              │    createdAt: Long   │
                              └──────────┬──────────┘
                                         │
                                         │ 1:N
                                         ▼
                              ┌─────────────────────┐
                              │    DailyRating      │
                              ├─────────────────────┤
                              │ PK id: Long         │
                              │    date: LocalDate  │
                              │ FK categoryId: Long?│
                              │    ratingValue      │
                              │    updatedAt: Long  │
                              └─────────────────────┘
```

## Entities

### FamilyMember

Represents a configured family member (spouse or child) whose name appears in personalized rating categories.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | Long | PK, auto-generate | Unique identifier |
| `name` | String | Not blank, max 100 chars | Display name (e.g., "Alex", "Emma") |
| `relationshipType` | RelationshipType | Not null | SPOUSE or CHILD |
| `createdAt` | Long | Not null | Unix timestamp of creation |

**Validation Rules**:
- Name must be 1-100 characters, trimmed
- Only one SPOUSE allowed at a time
- No limit on CHILD count (soft UI guidance at 6+)

**State Transitions**: N/A (static once created, can be deleted)

---

### Category

Represents a ratable aspect of the user's day. Categories can be default (built-in), family-member-based (auto-created), or custom (user-defined).

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | Long | PK, auto-generate | Unique identifier |
| `name` | String | Not blank, max 100 chars | Display name |
| `type` | CategoryType | Not null | DEFAULT, SPOUSE, CHILD, or CUSTOM |
| `familyMemberId` | Long? | FK to FamilyMember, nullable | Links SPOUSE/CHILD categories to their person |
| `displayOrder` | Int | >= 0 | Ordering for UI display |
| `isActive` | Boolean | Not null | False = soft-deleted (hidden but data preserved) |
| `createdAt` | Long | Not null | Unix timestamp of creation |

**CategoryType Enum**:
```kotlin
enum class CategoryType {
    DEFAULT,  // Overall Day, Physical Activity, Emotional State, Self-Care
    SPOUSE,   // Auto-created when spouse is configured
    CHILD,    // Auto-created for each child
    CUSTOM    // User-defined categories
}
```

**Default Categories** (seeded on first launch):
| Name | Type | Display Order |
|------|------|---------------|
| Overall Day | DEFAULT | 0 |
| Physical Activity | DEFAULT | 10 |
| Emotional State | DEFAULT | 20 |
| Self-Care | DEFAULT | 30 |

**Validation Rules**:
- Name must be 1-100 characters, trimmed
- `familyMemberId` required if type is SPOUSE or CHILD, null otherwise
- `displayOrder` determines UI sort order (lower = first)

**State Transitions**:
- Active → Inactive (soft delete via `isActive = false`)
- Name can be updated (affects future display, not historical)

---

### DailyRating

Represents a single rating for a specific category on a specific date.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | Long | PK, auto-generate | Unique identifier |
| `date` | LocalDate | Not null, indexed | Calendar date of rating |
| `categoryId` | Long? | FK to Category (SET_NULL on delete) | Which category was rated |
| `ratingValue` | RatingValue | Not null | The emoji rating |
| `updatedAt` | Long | Not null | Unix timestamp of last update |

**RatingValue Enum**:
```kotlin
enum class RatingValue(val emoji: String, val label: String) {
    NEGATIVE("😢", "Sad"),
    NEUTRAL("😐", "Neutral"),
    POSITIVE("😊", "Happy")
}
```

**Validation Rules**:
- Unique constraint on (date, categoryId) — one rating per category per day
- `categoryId` becomes null if category is deleted (preserves historical data)

**State Transitions**:
- Rating can be updated multiple times per day (last value wins)
- Historical ratings (past dates) are read-only in UI

---

## Relationships

| From | To | Cardinality | Description |
|------|----|-------------|-------------|
| Category | FamilyMember | N:1 (optional) | SPOUSE/CHILD categories link to a person |
| DailyRating | Category | N:1 (optional) | Ratings belong to a category (nullable for deleted categories) |

---

## Indexes

| Table | Index | Columns | Purpose |
|-------|-------|---------|---------|
| daily_ratings | idx_ratings_date | date | Fast lookup for "today" and history queries |
| daily_ratings | idx_ratings_category | categoryId | Fast lookup by category |
| categories | idx_categories_active | isActive, displayOrder | Fast fetch of active categories in order |

---

## Type Converters (Room)

```kotlin
class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString() // ISO-8601
    
    @TypeConverter
    fun toLocalDate(value: String): LocalDate = LocalDate.parse(value)
    
    @TypeConverter
    fun fromCategoryType(type: CategoryType): String = type.name
    
    @TypeConverter
    fun toCategoryType(value: String): CategoryType = CategoryType.valueOf(value)
    
    @TypeConverter
    fun fromRatingValue(rating: RatingValue): String = rating.name
    
    @TypeConverter
    fun toRatingValue(value: String): RatingValue = RatingValue.valueOf(value)
    
    @TypeConverter
    fun fromRelationshipType(type: RelationshipType): String = type.name
    
    @TypeConverter
    fun toRelationshipType(value: String): RelationshipType = RelationshipType.valueOf(value)
}
```

---

## Domain Models (UI Layer)

Separate from Room entities for clean architecture:

```kotlin
// Immutable domain models used in UI
data class Category(
    val id: Long,
    val name: String,
    val type: CategoryType,
    val familyMemberName: String? = null // Denormalized for display
)

data class Rating(
    val categoryId: Long,
    val categoryName: String,
    val value: RatingValue?
)

data class DayRatings(
    val date: LocalDate,
    val ratings: List<Rating>,
    val isComplete: Boolean // All active categories rated
)

data class FamilyMember(
    val id: Long,
    val name: String,
    val relationshipType: RelationshipType
)
```

---

## Migration Strategy

**Version 1** (initial): Create all tables as defined above.

Future migrations will use Room's built-in migration support:
```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Example: Add new column
        database.execSQL("ALTER TABLE categories ADD COLUMN icon TEXT")
    }
}
```
