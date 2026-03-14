package com.product.hstudio.simpletodo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.product.hstudio.simpletodo.domain.usecase.AddTodoUseCase
import com.product.hstudio.simpletodo.domain.usecase.DeleteTodoUseCase
import com.product.hstudio.simpletodo.domain.usecase.GetTodosUseCase
import com.product.hstudio.simpletodo.domain.usecase.ToggleTodoUseCase
import com.product.hstudio.simpletodo.domain.usecase.UpdateTodoUseCase

class TodoViewModelFactory(
    private val getTodosUseCase: GetTodosUseCase,
    private val addTodoUseCase: AddTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val toggleTodoUseCase: ToggleTodoUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TodoViewModel(
            getTodosUseCase,
            addTodoUseCase,
            updateTodoUseCase,
            deleteTodoUseCase,
            toggleTodoUseCase
        ) as T
    }
}
