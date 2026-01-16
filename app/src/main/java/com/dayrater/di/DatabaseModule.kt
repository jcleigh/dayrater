package com.dayrater.di

import android.content.Context
import androidx.room.Room
import com.dayrater.data.local.DayRaterDatabase
import com.dayrater.data.local.dao.CategoryDao
import com.dayrater.data.local.dao.FamilyMemberDao
import com.dayrater.data.local.dao.RatingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Hilt module providing database dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provides a coroutine scope for database operations.
     */
    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob())
    
    /**
     * Provides the Room database instance.
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        scope: CoroutineScope
    ): DayRaterDatabase {
        return Room.databaseBuilder(
            context,
            DayRaterDatabase::class.java,
            DayRaterDatabase.DATABASE_NAME
        )
            .addCallback(DayRaterDatabase.createCallback(scope))
            .build()
    }
    
    /**
     * Provides the CategoryDao.
     */
    @Provides
    fun provideCategoryDao(database: DayRaterDatabase): CategoryDao {
        return database.categoryDao()
    }
    
    /**
     * Provides the FamilyMemberDao.
     */
    @Provides
    fun provideFamilyMemberDao(database: DayRaterDatabase): FamilyMemberDao {
        return database.familyMemberDao()
    }
    
    /**
     * Provides the RatingDao.
     */
    @Provides
    fun provideRatingDao(database: DayRaterDatabase): RatingDao {
        return database.ratingDao()
    }
}
