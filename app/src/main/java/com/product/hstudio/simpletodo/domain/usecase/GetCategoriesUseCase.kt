package com.product.hstudio.simpletodo.domain.usecase

import com.product.hstudio.simpletodo.domain.model.Category
import com.product.hstudio.simpletodo.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> = repository.getCategories()
}
