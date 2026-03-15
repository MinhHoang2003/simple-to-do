package com.product.hstudio.simpletodo.domain.repository

import com.product.hstudio.simpletodo.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<List<Category>>
    suspend fun addCategory(category: Category): Long
    suspend fun deleteCategory(category: Category)
}
