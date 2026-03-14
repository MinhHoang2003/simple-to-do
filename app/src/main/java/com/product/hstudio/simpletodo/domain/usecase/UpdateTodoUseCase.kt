package com.product.hstudio.simpletodo.domain.usecase

import com.product.hstudio.simpletodo.domain.model.Todo
import com.product.hstudio.simpletodo.domain.repository.TodoRepository

class UpdateTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(todo: Todo) = repository.update(todo)
}
