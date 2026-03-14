package com.product.hstudio.simpletodo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.product.hstudio.simpletodo.data.local.database.TodoDatabase
import com.product.hstudio.simpletodo.data.repository.TodoRepositoryImpl
import com.product.hstudio.simpletodo.domain.usecase.AddTodoUseCase
import com.product.hstudio.simpletodo.domain.usecase.DeleteTodoUseCase
import com.product.hstudio.simpletodo.domain.usecase.GetTodosUseCase
import com.product.hstudio.simpletodo.domain.usecase.ToggleTodoUseCase
import com.product.hstudio.simpletodo.domain.usecase.UpdateTodoUseCase
import com.product.hstudio.simpletodo.ui.screen.TodoListScreen
import com.product.hstudio.simpletodo.ui.theme.SimpleTodoTheme

class MainActivity : ComponentActivity() {

    private val database by lazy { TodoDatabase.getDatabase(applicationContext) }
    private val repository by lazy { TodoRepositoryImpl(database.todoDao()) }

    private val viewModel: TodoViewModel by viewModels {
        TodoViewModelFactory(
            GetTodosUseCase(repository),
            AddTodoUseCase(repository),
            UpdateTodoUseCase(repository),
            DeleteTodoUseCase(repository),
            ToggleTodoUseCase(repository)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleTodoTheme {
                TodoListScreen(viewModel = viewModel)
            }
        }
    }
}
