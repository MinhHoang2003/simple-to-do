package com.product.hstudio.simpletodo.data

import kotlinx.coroutines.flow.Flow

class TodoRepository(private val dao: TodoDao) {

    val allTodos: Flow<List<Todo>> = dao.getAllTodos()

    suspend fun insert(todo: Todo) = dao.insert(todo)

    suspend fun update(todo: Todo) = dao.update(todo)

    suspend fun delete(todo: Todo) = dao.delete(todo)
}
