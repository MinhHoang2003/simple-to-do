package com.product.hstudio.simpletodo.data.repository

import com.product.hstudio.simpletodo.data.local.dao.CategoryDao
import com.product.hstudio.simpletodo.data.local.entity.toDomain
import com.product.hstudio.simpletodo.data.local.entity.toEntity
import com.product.hstudio.simpletodo.domain.model.Category
import com.product.hstudio.simpletodo.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao
) : CategoryRepository {

    override fun getCategories(): Flow<List<Category>> =
        dao.getAllCategories().map { list -> list.map { it.toDomain() } }

    override suspend fun addCategory(category: Category): Long =
        dao.insert(category.toEntity())

    override suspend fun deleteCategory(category: Category) =
        dao.delete(category.toEntity())
}
