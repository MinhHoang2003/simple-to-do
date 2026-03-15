package com.product.hstudio.simpletodo.fake

import com.product.hstudio.simpletodo.domain.model.Category
import com.product.hstudio.simpletodo.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCategoryRepository : CategoryRepository {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())

    val addCalls = mutableListOf<Category>()
    val deleteCalls = mutableListOf<Category>()

    fun emit(categories: List<Category>) {
        _categories.value = categories
    }

    override fun getCategories(): Flow<List<Category>> = _categories

    override suspend fun addCategory(category: Category): Long {
        addCalls.add(category)
        return addCalls.size.toLong()
    }

    override suspend fun deleteCategory(category: Category) {
        deleteCalls.add(category)
    }
}
