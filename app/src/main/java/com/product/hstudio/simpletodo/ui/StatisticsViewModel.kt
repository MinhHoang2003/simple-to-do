package com.product.hstudio.simpletodo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.product.hstudio.simpletodo.domain.model.Priority
import com.product.hstudio.simpletodo.domain.model.Todo
import com.product.hstudio.simpletodo.domain.usecase.GetTodosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

data class DailyStat(val day: Int, val total: Int, val completed: Int)

data class StatisticsUiState(
    val yearMonth: YearMonth = YearMonth.now(),
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val highCount: Int = 0,
    val mediumCount: Int = 0,
    val lowCount: Int = 0,
    val dailyStats: List<DailyStat> = emptyList()
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    getTodosUseCase: GetTodosUseCase
) : ViewModel() {

    private val _yearMonth = MutableStateFlow(YearMonth.now())

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(getTodosUseCase(), _yearMonth) { todos, ym ->
                computeStats(ym, todos)
            }.collect { _uiState.value = it }
        }
    }

    fun previousMonth() = _yearMonth.update { it.minusMonths(1) }
    fun nextMonth() = _yearMonth.update { it.plusMonths(1) }

    private fun computeStats(ym: YearMonth, todos: List<Todo>): StatisticsUiState {
        val zone = ZoneId.systemDefault()
        val monthTodos = todos.filter { todo ->
            todo.dueDate?.let { millis ->
                val date = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
                YearMonth.of(date.year, date.month) == ym
            } ?: false
        }

        val dailyStats = (1..ym.lengthOfMonth()).map { day ->
            val dayTodos = monthTodos.filter { todo ->
                val date = Instant.ofEpochMilli(todo.dueDate!!).atZone(zone).toLocalDate()
                date.dayOfMonth == day
            }
            DailyStat(day, dayTodos.size, dayTodos.count { it.isCompleted })
        }

        return StatisticsUiState(
            yearMonth = ym,
            totalTasks = monthTodos.size,
            completedTasks = monthTodos.count { it.isCompleted },
            highCount = monthTodos.count { it.priority == Priority.HIGH },
            mediumCount = monthTodos.count { it.priority == Priority.MEDIUM },
            lowCount = monthTodos.count { it.priority == Priority.LOW },
            dailyStats = dailyStats
        )
    }
}
