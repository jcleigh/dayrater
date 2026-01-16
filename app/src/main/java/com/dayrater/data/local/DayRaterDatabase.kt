package com.dayrater.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dayrater.data.local.dao.CategoryDao
import com.dayrater.data.local.dao.FamilyMemberDao
import com.dayrater.data.local.dao.RatingDao
import com.dayrater.data.local.entity.CategoryEntity
import com.dayrater.data.local.entity.CategoryType
import com.dayrater.data.local.entity.DailyRatingEntity
import com.dayrater.data.local.entity.FamilyMemberEntity
import com.dayrater.data.local.entity.RelationshipType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room database for DayRater.
 * Contains tables for categories, family members, and daily ratings.
 */
@Database(
    entities = [
        CategoryEntity::class,
        FamilyMemberEntity::class,
        DailyRatingEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class DayRaterDatabase : RoomDatabase() {
    
    abstract fun categoryDao(): CategoryDao
    abstract fun familyMemberDao(): FamilyMemberDao
    abstract fun ratingDao(): RatingDao
    
    companion object {
        const val DATABASE_NAME = "dayrater.db"
        
        /**
         * Default categories to seed on first launch.
         */
        val DEFAULT_CATEGORIES = listOf(
            CategoryEntity(
                name = "Overall Day",
                type = CategoryType.DEFAULT,
                displayOrder = 0
            ),
            CategoryEntity(
                name = "Physical Activity",
                type = CategoryType.DEFAULT,
                displayOrder = 10
            ),
            CategoryEntity(
                name = "Emotional State",
                type = CategoryType.DEFAULT,
                displayOrder = 20
            ),
            CategoryEntity(
                name = "Self-Care",
                type = CategoryType.DEFAULT,
                displayOrder = 30
            )
        )
        
        /**
         * Creates a callback that seeds default data on database creation.
         * Seeds:
         * - Default categories (Overall Day, Physical Activity, Emotional State, Self-Care)
         * - Self family member
         */
        fun createCallback(scope: CoroutineScope): Callback {
            return object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    scope.launch(Dispatchers.IO) {
                        val currentTime = System.currentTimeMillis()
                        
                        // Seed default categories
                        DEFAULT_CATEGORIES.forEach { category ->
                            db.execSQL(
                                """
                                INSERT INTO categories (name, type, displayOrder, isActive, createdAt)
                                VALUES (?, ?, ?, 1, ?)
                                """.trimIndent(),
                                arrayOf(
                                    category.name,
                                    category.type.name,
                                    category.displayOrder,
                                    currentTime
                                )
                            )
                        }
                        
                        // Seed self family member
                        db.execSQL(
                            """
                            INSERT INTO family_members (name, relationship, displayOrder, isActive, createdAt)
                            VALUES (?, ?, ?, 1, ?)
                            """.trimIndent(),
                            arrayOf(
                                "Me",
                                RelationshipType.SELF.name,
                                0,
                                currentTime
                            )
                        )
                    }
                }
            }
        }
    }
}
