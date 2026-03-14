package com.product.hstudio.simpletodo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.product.hstudio.simpletodo.data.Todo
import com.product.hstudio.simpletodo.data.TodoDatabase
import com.product.hstudio.simpletodo.data.TodoRepository
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TodoRepository
    val allTodos: LiveData<List<Todo>>

    init {
        val dao = TodoDatabase.getDatabase(application).todoDao()
        repository = TodoRepository(dao)
        allTodos = repository.allTodos.asLiveData()
    }

    fun insert(todo: Todo) = viewModelScope.launch { repository.insert(todo) }

    fun update(todo: Todo) = viewModelScope.launch { repository.update(todo) }

    fun delete(todo: Todo) = viewModelScope.launch { repository.delete(todo) }

    fun toggleComplete(todo: Todo) = update(todo.copy(isCompleted = !todo.isCompleted))
}
