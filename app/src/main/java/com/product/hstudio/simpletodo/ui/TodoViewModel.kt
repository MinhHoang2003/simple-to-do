package com.product.hstudio.simpletodo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.product.hstudio.simpletodo.domain.model.Todo
import com.product.hstudio.simpletodo.domain.usecase.AddTodoUseCase
import com.product.hstudio.simpletodo.domain.usecase.DeleteTodoUseCase
import com.product.hstudio.simpletodo.domain.usecase.GetTodosUseCase
import com.product.hstudio.simpletodo.domain.usecase.ToggleTodoUseCase
import com.product.hstudio.simpletodo.domain.usecase.UpdateTodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TodoListUiState(
    val todos: List<Todo> = emptyList(),
    val showDialog: Boolean = false,
    val editingTodo: Todo? = null,
    val deletedTodo: Todo? = null
)

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val getTodosUseCase: GetTodosUseCase,
    private val addTodoUseCase: AddTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val toggleTodoUseCase: ToggleTodoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoListUiState())
    val uiState: StateFlow<TodoListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getTodosUseCase().collect { todos ->
                _uiState.update { it.copy(todos = todos) }
            }
        }
    }

    fun showAddDialog() = _uiState.update { it.copy(showDialog = true, editingTodo = null) }

    fun showEditDialog(todo: Todo) = _uiState.update { it.copy(showDialog = true, editingTodo = todo) }

    fun dismissDialog() = _uiState.update { it.copy(showDialog = false, editingTodo = null) }

    fun saveTodo(todo: Todo) = viewModelScope.launch {
        if (todo.id == 0) addTodoUseCase(todo) else updateTodoUseCase(todo)
        dismissDialog()
    }

    fun deleteTodo(todo: Todo) = viewModelScope.launch {
        deleteTodoUseCase(todo)
        _uiState.update { it.copy(deletedTodo = todo) }
    }

    fun undoDelete(todo: Todo) = viewModelScope.launch {
        addTodoUseCase(todo)
        _uiState.update { it.copy(deletedTodo = null) }
    }

    fun clearDeletedTodo() = _uiState.update { it.copy(deletedTodo = null) }

    fun toggleComplete(todo: Todo) = viewModelScope.launch { toggleTodoUseCase(todo) }
}
