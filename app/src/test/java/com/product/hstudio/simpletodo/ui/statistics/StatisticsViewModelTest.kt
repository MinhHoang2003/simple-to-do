package com.product.hstudio.simpletodo.ui.statistics

import com.product.hstudio.simpletodo.domain.model.Priority
import com.product.hstudio.simpletodo.domain.model.Todo
import com.product.hstudio.simpletodo.domain.usecase.GetTodosUseCase
import com.product.hstudio.simpletodo.fake.FakeTodoRepository
import com.product.hstudio.simpletodo.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeTodoRepository
    private lateinit var viewModel: StatisticsViewModel

    // A fixed month to test against: March 2026
    private val testYearMonth = YearMonth.of(2026, 3)

    private fun epochMilliFor(year: Int, month: Int, day: Int): Long =
        LocalDate.of(year, month, day)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

    @Before
    fun setUp() {
        repository = FakeTodoRepository()
        viewModel = StatisticsViewModel(GetTodosUseCase(repository))

        // Navigate to March 2026 regardless of when the test runs
        val current = viewModel.uiState.value.yearMonth
        val monthsToJump = (testYearMonth.year - current.year) * 12 +
                (testYearMonth.monthValue - current.monthValue)
        repeat(Math.abs(monthsToJump)) {
            if (monthsToJump > 0) viewModel.nextMonth() else viewModel.previousMonth()
        }
    }

    // region month navigation

    @Test
    fun `previousMonth decrements month by one`() = runTest {
        val before = viewModel.uiState.value.yearMonth
        viewModel.previousMonth()
        assertEquals(before.minusMonths(1), viewModel.uiState.value.yearMonth)
    }

    @Test
    fun `nextMonth increments month by one`() = runTest {
        val before = viewModel.uiState.value.yearMonth
        viewModel.nextMonth()
        assertEquals(before.plusMonths(1), viewModel.uiState.value.yearMonth)
    }

    // endregion

    // region computeStats — null dueDate branch

    @Test
    fun `todo with null dueDate is excluded from monthly stats`() = runTest {
        repository.emit(listOf(Todo(id = 1, title = "No date", dueDate = null)))

        assertEquals(0, viewModel.uiState.value.totalTasks)
    }

    // endregion

    // region computeStats — month matching branch

    @Test
    fun `todo in a different month is excluded`() = runTest {
        // February 2026 — different from testYearMonth (March 2026)
        val feb = epochMilliFor(2026, 2, 15)
        repository.emit(listOf(Todo(id = 1, title = "Feb task", dueDate = feb)))

        assertEquals(0, viewModel.uiState.value.totalTasks)
    }

    @Test
    fun `todo in the current month is included`() = runTest {
        val march15 = epochMilliFor(2026, 3, 15)
        repository.emit(listOf(Todo(id = 1, title = "March task", dueDate = march15)))

        assertEquals(1, viewModel.uiState.value.totalTasks)
    }

    // endregion

    // region computeStats — completed count branches

    @Test
    fun `completed and incomplete tasks are counted separately`() = runTest {
        val march10 = epochMilliFor(2026, 3, 10)
        repository.emit(listOf(
            Todo(id = 1, title = "Done", isCompleted = true, dueDate = march10),
            Todo(id = 2, title = "Pending", isCompleted = false, dueDate = march10)
        ))

        val state = viewModel.uiState.value
        assertEquals(2, state.totalTasks)
        assertEquals(1, state.completedTasks)
    }

    @Test
    fun `all tasks completed gives completedTasks equal to totalTasks`() = runTest {
        val march5 = epochMilliFor(2026, 3, 5)
        repository.emit(listOf(
            Todo(id = 1, title = "A", isCompleted = true, dueDate = march5),
            Todo(id = 2, title = "B", isCompleted = true, dueDate = march5)
        ))

        val state = viewModel.uiState.value
        assertEquals(state.totalTasks, state.completedTasks)
    }

    // endregion

    // region computeStats — priority branches

    @Test
    fun `HIGH priority todos are counted in highCount`() = runTest {
        val march1 = epochMilliFor(2026, 3, 1)
        repository.emit(listOf(
            Todo(id = 1, title = "H", priority = Priority.HIGH, dueDate = march1),
            Todo(id = 2, title = "H2", priority = Priority.HIGH, dueDate = march1)
        ))

        assertEquals(2, viewModel.uiState.value.highCount)
    }

    @Test
    fun `MEDIUM priority todos are counted in mediumCount`() = runTest {
        val march2 = epochMilliFor(2026, 3, 2)
        repository.emit(listOf(
            Todo(id = 1, title = "M", priority = Priority.MEDIUM, dueDate = march2)
        ))

        assertEquals(1, viewModel.uiState.value.mediumCount)
    }

    @Test
    fun `LOW priority todos are counted in lowCount`() = runTest {
        val march3 = epochMilliFor(2026, 3, 3)
        repository.emit(listOf(
            Todo(id = 1, title = "L", priority = Priority.LOW, dueDate = march3)
        ))

        assertEquals(1, viewModel.uiState.value.lowCount)
    }

    @Test
    fun `priority counts are independent of each other`() = runTest {
        val march1 = epochMilliFor(2026, 3, 1)
        repository.emit(listOf(
            Todo(id = 1, title = "H", priority = Priority.HIGH, dueDate = march1),
            Todo(id = 2, title = "M", priority = Priority.MEDIUM, dueDate = march1),
            Todo(id = 3, title = "L", priority = Priority.LOW, dueDate = march1)
        ))

        val state = viewModel.uiState.value
        assertEquals(1, state.highCount)
        assertEquals(1, state.mediumCount)
        assertEquals(1, state.lowCount)
        assertEquals(3, state.totalTasks)
    }

    // endregion

    // region computeStats — daily grouping

    @Test
    fun `dailyStats groups todos by day correctly`() = runTest {
        val march10 = epochMilliFor(2026, 3, 10)
        val march15 = epochMilliFor(2026, 3, 15)
        repository.emit(listOf(
            Todo(id = 1, title = "A", dueDate = march10),
            Todo(id = 2, title = "B", dueDate = march10),
            Todo(id = 3, title = "C", dueDate = march15)
        ))

        val daily = viewModel.uiState.value.dailyStats
        assertEquals(2, daily.first { it.day == 10 }.total)
        assertEquals(1, daily.first { it.day == 15 }.total)
    }

    @Test
    fun `dailyStats has entries for every day of the month`() = runTest {
        repository.emit(emptyList())

        val daily = viewModel.uiState.value.dailyStats
        assertEquals(testYearMonth.lengthOfMonth(), daily.size)
    }

    @Test
    fun `daily completed count reflects only completed todos for that day`() = runTest {
        val march20 = epochMilliFor(2026, 3, 20)
        repository.emit(listOf(
            Todo(id = 1, title = "Done", isCompleted = true, dueDate = march20),
            Todo(id = 2, title = "Not done", isCompleted = false, dueDate = march20)
        ))

        val dayStat = viewModel.uiState.value.dailyStats.first { it.day == 20 }
        assertEquals(2, dayStat.total)
        assertEquals(1, dayStat.completed)
    }

    // endregion

    // region reactive updates

    @Test
    fun `stats update reactively when repository emits new todos`() = runTest {
        val march1 = epochMilliFor(2026, 3, 1)

        repository.emit(emptyList())
        assertEquals(0, viewModel.uiState.value.totalTasks)

        repository.emit(listOf(Todo(id = 1, title = "New", dueDate = march1)))
        assertEquals(1, viewModel.uiState.value.totalTasks)
    }

    // endregion
}
