package com.product.hstudio.simpletodo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.product.hstudio.simpletodo.ui.screen.TodoListScreen
import com.product.hstudio.simpletodo.ui.theme.SimpleTodoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleTodoTheme {
                TodoListScreen()
            }
        }
    }
}
