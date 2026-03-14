package com.product.hstudio.simpletodo.domain.usecase

import com.product.hstudio.simpletodo.domain.model.Todo
import com.product.hstudio.simpletodo.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTodosUseCase(private val repository: TodoRepository) {
    operator fun invoke(): Flow<List<Todo>> = repository.getTodos().map { todos ->
        todos.sortedWith(
            compareBy<Todo> { it.isCompleted }
                .thenByDescending { it.priority.ordinal }
                .thenByDescending { it.createdAt }
        )
    }
}
