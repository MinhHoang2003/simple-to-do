package com.product.hstudio.simpletodo.fake

import com.product.hstudio.simpletodo.domain.model.Todo
import com.product.hstudio.simpletodo.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeTodoRepository : TodoRepository {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())

    val addCalls = mutableListOf<Todo>()
    val updateCalls = mutableListOf<Todo>()
    val deleteCalls = mutableListOf<Todo>()

    fun emit(todos: List<Todo>) {
        _todos.value = todos
    }

    override fun getTodos(): Flow<List<Todo>> = _todos

    override suspend fun add(todo: Todo) {
        addCalls.add(todo)
    }

    override suspend fun update(todo: Todo) {
        updateCalls.add(todo)
    }

    override suspend fun delete(todo: Todo) {
        deleteCalls.add(todo)
    }
}
