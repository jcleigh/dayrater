package com.dayrater.di

import com.dayrater.data.repository.FamilyRepository
import com.dayrater.data.repository.FamilyRepositoryImpl
import com.dayrater.data.repository.RatingRepository
import com.dayrater.data.repository.RatingRepositoryImpl
import com.dayrater.data.repository.SettingsRepository
import com.dayrater.data.repository.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing repository bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindRatingRepository(
        ratingRepositoryImpl: RatingRepositoryImpl
    ): RatingRepository
    
    @Binds
    @Singleton
    abstract fun bindFamilyRepository(
        familyRepositoryImpl: FamilyRepositoryImpl
    ): FamilyRepository
    
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
}
