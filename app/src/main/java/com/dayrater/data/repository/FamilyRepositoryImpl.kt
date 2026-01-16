package com.dayrater.data.repository

import com.dayrater.data.local.dao.FamilyMemberDao
import com.dayrater.data.local.entity.FamilyMemberEntity
import com.dayrater.data.local.entity.RelationshipType
import com.dayrater.domain.model.FamilyMember
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FamilyRepository using Room DAO.
 */
@Singleton
class FamilyRepositoryImpl @Inject constructor(
    private val familyMemberDao: FamilyMemberDao
) : FamilyRepository {
    
    override fun getActiveFamilyMembers(): Flow<List<FamilyMember>> {
        return familyMemberDao.getActiveFamilyMembers().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getAllFamilyMembers(): Flow<List<FamilyMember>> {
        return familyMemberDao.getAllFamilyMembers().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getSelf(): Flow<FamilyMember?> {
        return familyMemberDao.getSelf().map { it?.toDomainModel() }
    }
    
    override fun getSpouse(): Flow<FamilyMember?> {
        return familyMemberDao.getSpouse().map { it?.toDomainModel() }
    }
    
    override fun getChildren(): Flow<List<FamilyMember>> {
        return familyMemberDao.getChildren().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun addFamilyMember(name: String, relationship: RelationshipType): Long {
        // Get max display order
        val members = familyMemberDao.getActiveFamilyMembers().first()
        val maxOrder = members.maxOfOrNull { it.displayOrder } ?: 0
        
        val entity = FamilyMemberEntity(
            name = name,
            relationship = relationship,
            displayOrder = maxOrder + 10
        )
        return familyMemberDao.insert(entity)
    }
    
    override suspend fun updateName(memberId: Long, name: String) {
        familyMemberDao.updateName(memberId, name)
    }
    
    override suspend fun deleteFamilyMember(memberId: Long) {
        familyMemberDao.softDelete(memberId)
    }
    
    override suspend fun restoreFamilyMember(memberId: Long) {
        familyMemberDao.restore(memberId)
    }
    
    override suspend fun hasSpouse(): Boolean {
        return familyMemberDao.getSpouse().first() != null
    }
    
    override suspend fun initializeSelf() {
        val existingSelf = familyMemberDao.getSelf().first()
        if (existingSelf == null) {
            val selfEntity = FamilyMemberEntity(
                name = "Me",
                relationship = RelationshipType.SELF,
                displayOrder = 0
            )
            familyMemberDao.insert(selfEntity)
        }
    }
    
    private fun FamilyMemberEntity.toDomainModel() = FamilyMember(
        id = id,
        name = name,
        relationship = relationship,
        displayOrder = displayOrder,
        isActive = isActive
    )
}
