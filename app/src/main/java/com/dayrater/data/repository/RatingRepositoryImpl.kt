package com.dayrater.data.repository

import com.dayrater.data.local.dao.CategoryDao
import com.dayrater.data.local.dao.FamilyMemberDao
import com.dayrater.data.local.dao.RatingDao
import com.dayrater.data.local.entity.CategoryEntity
import com.dayrater.data.local.entity.CategoryType
import com.dayrater.data.local.entity.DailyRatingEntity
import com.dayrater.data.local.entity.RatingValue
import com.dayrater.domain.model.Category
import com.dayrater.domain.model.DayRatings
import com.dayrater.domain.model.FamilyMember
import com.dayrater.domain.model.Rating
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of RatingRepository using Room DAOs.
 */
@Singleton
class RatingRepositoryImpl @Inject constructor(
    private val ratingDao: RatingDao,
    private val categoryDao: CategoryDao,
    private val familyMemberDao: FamilyMemberDao
) : RatingRepository {
    
    // ==================== Rating Operations ====================
    
    override fun getRatingsForDate(date: LocalDate, familyMemberId: Long): Flow<DayRatings?> {
        return combine(
            ratingDao.getRatingsForDate(date, familyMemberId),
            categoryDao.getActiveCategories(),
            familyMemberDao.getById(familyMemberId)
        ) { ratings, categories, familyMember ->
            if (familyMember == null) return@combine null
            
            val categoryMap = categories.associateBy { it.id }
            val ratingList = ratings.mapNotNull { entity ->
                val catId = entity.categoryId ?: return@mapNotNull null
                val category = categoryMap[catId] ?: return@mapNotNull null
                Rating(
                    categoryId = catId,
                    categoryName = category.name,
                    value = entity.ratingValue
                )
            }
            
            DayRatings(
                date = date,
                familyMember = familyMember.toDomainModel(),
                ratings = ratingList
            )
        }
    }
    
    override fun getAllRatingsForDate(date: LocalDate): Flow<Map<Long, DayRatings>> {
        return combine(
            ratingDao.getAllRatingsForDate(date),
            categoryDao.getActiveCategories(),
            familyMemberDao.getActiveFamilyMembers()
        ) { ratings, categories, familyMembers ->
            val categoryMap = categories.associateBy { it.id }
            val familyMemberMap = familyMembers.associateBy { it.id }
            
            // Group ratings by family member
            val ratingsByMember = ratings.groupBy { it.familyMemberId }
            
            familyMembers.associate { member ->
                val memberRatings = ratingsByMember[member.id] ?: emptyList()
                val ratingList = memberRatings.mapNotNull { entity ->
                    val catId = entity.categoryId ?: return@mapNotNull null
                    val category = categoryMap[catId] ?: return@mapNotNull null
                    Rating(
                        categoryId = catId,
                        categoryName = category.name,
                        value = entity.ratingValue
                    )
                }
                
                member.id to DayRatings(
                    date = date,
                    familyMember = member.toDomainModel(),
                    ratings = ratingList
                )
            }
        }
    }
    
    override suspend fun saveRating(
        categoryId: Long,
        familyMemberId: Long,
        date: LocalDate,
        value: RatingValue
    ) {
        val entity = DailyRatingEntity(
            familyMemberId = familyMemberId,
            categoryId = categoryId,
            date = date,
            ratingValue = value
        )
        ratingDao.upsertRating(entity)
    }
    
    override suspend fun deleteRating(categoryId: Long, familyMemberId: Long, date: LocalDate) {
        ratingDao.deleteRating(familyMemberId, categoryId, date)
    }
    
    override fun getDatesWithRatings(): Flow<List<LocalDate>> {
        return ratingDao.getAllDatesWithRatings()
    }
    
    override fun getDatesWithRatingsForMonth(year: Int, month: Int): Flow<List<LocalDate>> {
        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.plusMonths(1).minusDays(1)
        return ratingDao.getDatesWithRatingsInRange(startDate, endDate)
    }
    
    // ==================== Category Operations ====================
    
    override fun getActiveCategories(): Flow<List<Category>> {
        return categoryDao.getActiveCategories().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun addCategory(name: String): Long {
        // Get the max display order and add 10
        val categories = categoryDao.getActiveCategories().first()
        val maxOrder = categories.maxOfOrNull { it.displayOrder } ?: 0
        
        val entity = CategoryEntity(
            name = name,
            type = CategoryType.CUSTOM,
            displayOrder = maxOrder + 10
        )
        return categoryDao.insert(entity)
    }
    
    override suspend fun renameCategory(categoryId: Long, newName: String) {
        categoryDao.renameCategory(categoryId, newName)
    }
    
    override suspend fun deleteCategory(categoryId: Long) {
        categoryDao.softDelete(categoryId)
    }
    
    override suspend fun restoreCategory(categoryId: Long) {
        categoryDao.restore(categoryId)
    }
    
    // ==================== Export Operations ====================
    
    override suspend fun exportToCsv(startDate: LocalDate?, endDate: LocalDate?): String {
        val ratings = getRatingsForExport(startDate, endDate)
        val categories = categoryDao.getActiveCategories().first()
        val familyMembers = familyMemberDao.getActiveFamilyMembers().first()
        
        val categoryMap = categories.associateBy { it.id }
        val memberMap = familyMembers.associateBy { it.id }
        
        val sb = StringBuilder()
        
        // Header row
        sb.appendLine("Date,Family Member,Category,Rating")
        
        // Data rows
        ratings.forEach { rating ->
            val memberName = memberMap[rating.familyMemberId]?.name ?: "Unknown"
            val categoryName = categoryMap[rating.categoryId]?.name ?: "Unknown"
            val date = rating.date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            
            sb.appendLine("$date,$memberName,$categoryName,${rating.ratingValue.label}")
        }
        
        return sb.toString()
    }
    
    override suspend fun exportToJson(startDate: LocalDate?, endDate: LocalDate?): String {
        val ratings = getRatingsForExport(startDate, endDate)
        val categories = categoryDao.getActiveCategories().first()
        val familyMembers = familyMemberDao.getActiveFamilyMembers().first()
        
        val categoryMap = categories.associateBy { it.id }
        val memberMap = familyMembers.associateBy { it.id }
        
        val sb = StringBuilder()
        sb.appendLine("{")
        sb.appendLine("  \"ratings\": [")
        
        ratings.forEachIndexed { index, rating ->
            val memberName = memberMap[rating.familyMemberId]?.name ?: "Unknown"
            val categoryName = categoryMap[rating.categoryId]?.name ?: "Unknown"
            val date = rating.date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val comma = if (index < ratings.size - 1) "," else ""
            
            sb.appendLine("    {")
            sb.appendLine("      \"date\": \"$date\",")
            sb.appendLine("      \"familyMember\": \"$memberName\",")
            sb.appendLine("      \"category\": \"$categoryName\",")
            sb.appendLine("      \"rating\": \"${rating.ratingValue.label}\",")
            sb.appendLine("      \"emoji\": \"${rating.ratingValue.emoji}\"")
            sb.appendLine("    }$comma")
        }
        
        sb.appendLine("  ]")
        sb.appendLine("}")
        
        return sb.toString()
    }
    
    private suspend fun getRatingsForExport(startDate: LocalDate?, endDate: LocalDate?): List<DailyRatingEntity> {
        return when {
            startDate != null && endDate != null -> {
                ratingDao.getRatingsInRange(startDate, endDate)
            }
            else -> {
                ratingDao.getAllRatings()
            }
        }
    }
    
    // ==================== Extension Functions ====================
    
    private fun CategoryEntity.toDomainModel() = Category(
        id = id,
        name = name,
        type = type,
        displayOrder = displayOrder,
        isActive = isActive
    )
    
    private fun com.dayrater.data.local.entity.FamilyMemberEntity.toDomainModel() = FamilyMember(
        id = id,
        name = name,
        relationship = relationship,
        displayOrder = displayOrder,
        isActive = isActive
    )
}
