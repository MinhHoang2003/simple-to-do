package com.product.hstudio.simpletodo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.product.hstudio.simpletodo.ui.screen.StatisticsScreen
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
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "todo_list") {
                    composable("todo_list") {
                        TodoListScreen(
                            onNavigateToStats = { navController.navigate("statistics") }
                        )
                    }
                    composable("statistics") {
                        StatisticsScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
