package com.dayrater.data.local

import androidx.room.TypeConverter
import com.dayrater.data.local.entity.CategoryType
import com.dayrater.data.local.entity.RatingValue
import com.dayrater.data.local.entity.RelationshipType
import java.time.LocalDate

/**
 * Room type converters for custom types.
 * Converts between database-storable types and domain types.
 */
class Converters {
    
    // LocalDate <-> String (ISO-8601)
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()
    
    @TypeConverter
    fun toLocalDate(value: String): LocalDate = LocalDate.parse(value)
    
    // CategoryType <-> String
    @TypeConverter
    fun fromCategoryType(type: CategoryType): String = type.name
    
    @TypeConverter
    fun toCategoryType(value: String): CategoryType = CategoryType.valueOf(value)
    
    // RatingValue <-> String
    @TypeConverter
    fun fromRatingValue(rating: RatingValue): String = rating.name
    
    @TypeConverter
    fun toRatingValue(value: String): RatingValue = RatingValue.valueOf(value)
    
    // RelationshipType <-> String
    @TypeConverter
    fun fromRelationshipType(type: RelationshipType): String = type.name
    
    @TypeConverter
    fun toRelationshipType(value: String): RelationshipType = RelationshipType.valueOf(value)
}
