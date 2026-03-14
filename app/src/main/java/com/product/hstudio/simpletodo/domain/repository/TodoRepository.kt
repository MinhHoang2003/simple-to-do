package com.product.hstudio.simpletodo.domain.repository

import com.product.hstudio.simpletodo.domain.model.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getTodos(): Flow<List<Todo>>
    suspend fun add(todo: Todo)
    suspend fun update(todo: Todo)
    suspend fun delete(todo: Todo)
}
