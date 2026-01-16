package com.dayrater.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Room entity representing a single rating for a category on a specific date.
 * One rating per family member per category per day (upsert on conflict).
 */
@Entity(
    tableName = "daily_ratings",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = FamilyMemberEntity::class,
            parentColumns = ["id"],
            childColumns = ["familyMemberId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["date"]),
        Index(value = ["categoryId"]),
        Index(value = ["familyMemberId"]),
        Index(value = ["date", "familyMemberId", "categoryId"], unique = true)
    ]
)
data class DailyRatingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /** Calendar date of the rating (device local time) */
    val date: LocalDate,
    
    /** Foreign key to FamilyMember; the person this rating belongs to */
    val familyMemberId: Long,
    
    /** Foreign key to Category; becomes null if category is deleted */
    val categoryId: Long?,
    
    /** The emoji rating value */
    val ratingValue: RatingValue,
    
    /** Unix timestamp of last update */
    val updatedAt: Long = System.currentTimeMillis()
)
