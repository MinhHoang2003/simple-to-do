package com.product.hstudio.simpletodo.data.repository

import com.product.hstudio.simpletodo.data.local.dao.TodoDao
import com.product.hstudio.simpletodo.data.local.entity.toDomain
import com.product.hstudio.simpletodo.data.local.entity.toEntity
import com.product.hstudio.simpletodo.domain.model.Todo
import com.product.hstudio.simpletodo.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TodoRepositoryImpl(private val dao: TodoDao) : TodoRepository {

    override fun getTodos(): Flow<List<Todo>> =
        dao.getAllTodos().map { entities -> entities.map { it.toDomain() } }

    override suspend fun add(todo: Todo) = dao.insert(todo.toEntity())

    override suspend fun update(todo: Todo) = dao.update(todo.toEntity())

    override suspend fun delete(todo: Todo) = dao.delete(todo.toEntity())
}
