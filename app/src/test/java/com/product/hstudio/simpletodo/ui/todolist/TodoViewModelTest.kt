package com.product.hstudio.simpletodo.ui.todolist

import com.product.hstudio.simpletodo.domain.model.Priority
import com.product.hstudio.simpletodo.domain.model.Todo
import com.product.hstudio.simpletodo.domain.usecase.AddTodoUseCase
import com.product.hstudio.simpletodo.domain.usecase.DeleteTodoUseCase
import com.product.hstudio.simpletodo.domain.usecase.GetTodosUseCase
import com.product.hstudio.simpletodo.domain.usecase.ToggleTodoUseCase
import com.product.hstudio.simpletodo.domain.usecase.UpdateTodoUseCase
import com.product.hstudio.simpletodo.fake.FakeTodoRepository
import com.product.hstudio.simpletodo.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeTodoRepository
    private lateinit var viewModel: TodoViewModel

    @Before
    fun setUp() {
        repository = FakeTodoRepository()
        viewModel = TodoViewModel(
            getTodosUseCase = GetTodosUseCase(repository),
            addTodoUseCase = AddTodoUseCase(repository),
            updateTodoUseCase = UpdateTodoUseCase(repository),
            deleteTodoUseCase = DeleteTodoUseCase(repository),
            toggleTodoUseCase = ToggleTodoUseCase(repository)
        )
    }

    // region todos state

    @Test
    fun `todos state reflects repository emissions`() = runTest {
        val todos = listOf(Todo(id = 1, title = "Task"))
        repository.emit(todos)

        assertEquals(todos, viewModel.uiState.value.todos)
    }

    // endregion

    // region selectTab branches

    @Test
    fun `selectTab(0) resets selectedDayEpochDay to today`() {
        // First move to tab 1 and select a different day
        viewModel.selectTab(1)
        val otherDay = LocalDate.now().minusDays(5).toEpochDay()
        viewModel.selectDay(otherDay)
        assertEquals(otherDay, viewModel.uiState.value.selectedDayEpochDay)

        // Switching back to tab 0 resets to today
        viewModel.selectTab(0)

        assertEquals(LocalDate.now().toEpochDay(), viewModel.uiState.value.selectedDayEpochDay)
        assertEquals(0, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun `selectTab(1) preserves the current selectedDayEpochDay`() {
        val customDay = LocalDate.now().minusDays(3).toEpochDay()
        viewModel.selectDay(customDay)

        viewModel.selectTab(1)

        assertEquals(customDay, viewModel.uiState.value.selectedDayEpochDay)
        assertEquals(1, viewModel.uiState.value.selectedTab)
    }

    // endregion

    // region saveTodo branches (id == 0 → add, id != 0 → update)

    @Test
    fun `saveTodo with id 0 calls addTodoUseCase`() = runTest {
        val newTodo = Todo(id = 0, title = "Brand new")

        viewModel.saveTodo(newTodo)

        assertEquals(1, repository.addCalls.size)
        assertTrue(repository.updateCalls.isEmpty())
        assertEquals("Brand new", repository.addCalls[0].title)
    }

    @Test
    fun `saveTodo with non-zero id calls updateTodoUseCase`() = runTest {
        val existingTodo = Todo(id = 5, title = "Existing")

        viewModel.saveTodo(existingTodo)

        assertEquals(1, repository.updateCalls.size)
        assertTrue(repository.addCalls.isEmpty())
        assertEquals(5, repository.updateCalls[0].id)
    }

    @Test
    fun `saveTodo dismisses the dialog`() = runTest {
        viewModel.showAddDialog()
        assertTrue(viewModel.uiState.value.showDialog)

        viewModel.saveTodo(Todo(id = 0, title = "Task"))

        assertFalse(viewModel.uiState.value.showDialog)
    }

    // endregion

    // region showAddDialog / showEditDialog / dismissDialog

    @Test
    fun `showAddDialog sets showDialog true and clears editingTodo`() {
        viewModel.showAddDialog()

        val state = viewModel.uiState.value
        assertTrue(state.showDialog)
        assertNull(state.editingTodo)
        assertNull(state.preFillDate)
    }

    @Test
    fun `showEditDialog sets showDialog true and stores editingTodo`() {
        val todo = Todo(id = 3, title = "Edit me")
        viewModel.showEditDialog(todo)

        val state = viewModel.uiState.value
        assertTrue(state.showDialog)
        assertEquals(todo, state.editingTodo)
    }

    @Test
    fun `dismissDialog hides dialog and clears editingTodo`() {
        viewModel.showEditDialog(Todo(id = 1, title = "T"))
        viewModel.dismissDialog()

        val state = viewModel.uiState.value
        assertFalse(state.showDialog)
        assertNull(state.editingTodo)
    }

    // endregion

    // region showAddDialogForDay

    @Test
    fun `showAddDialogForDay sets showDialog and preFillDate`() {
        val epochDay = LocalDate.of(2026, 3, 15).toEpochDay()
        viewModel.showAddDialogForDay(epochDay)

        val state = viewModel.uiState.value
        assertTrue(state.showDialog)
        assertNull(state.editingTodo)
        assertNotNull(state.preFillDate)
    }

    // endregion

    // region selectDay

    @Test
    fun `selectDay updates selectedDayEpochDay`() {
        val day = LocalDate.of(2026, 3, 10).toEpochDay()
        viewModel.selectDay(day)

        assertEquals(day, viewModel.uiState.value.selectedDayEpochDay)
    }

    // endregion

    // region deleteTodo / undoDelete / clearDeletedTodo

    @Test
    fun `deleteTodo calls repository and sets deletedTodo`() = runTest {
        val todo = Todo(id = 7, title = "Delete me")
        viewModel.deleteTodo(todo)

        assertEquals(1, repository.deleteCalls.size)
        assertEquals(todo, viewModel.uiState.value.deletedTodo)
    }

    @Test
    fun `undoDelete re-adds todo and clears deletedTodo`() = runTest {
        val todo = Todo(id = 7, title = "Restore me")
        viewModel.deleteTodo(todo)
        viewModel.undoDelete(todo)

        assertEquals(1, repository.addCalls.size)
        assertNull(viewModel.uiState.value.deletedTodo)
    }

    @Test
    fun `clearDeletedTodo sets deletedTodo to null`() = runTest {
        val todo = Todo(id = 7, title = "Clear me")
        viewModel.deleteTodo(todo)
        assertNotNull(viewModel.uiState.value.deletedTodo)

        viewModel.clearDeletedTodo()
        assertNull(viewModel.uiState.value.deletedTodo)
    }

    // endregion

    // region toggleComplete

    @Test
    fun `toggleComplete delegates to toggleTodoUseCase`() = runTest {
        val todo = Todo(id = 1, title = "Toggle me", isCompleted = false)
        viewModel.toggleComplete(todo)

        assertEquals(1, repository.updateCalls.size)
        assertTrue(repository.updateCalls[0].isCompleted)
    }

    // endregion

    // region quickAddTodo

    @Test
    fun `quickAddTodo adds a todo with the given title and computed dueDate`() = runTest {
        val epochDay = LocalDate.of(2026, 3, 15).toEpochDay()
        viewModel.quickAddTodo("Quick task", epochDay)

        assertEquals(1, repository.addCalls.size)
        assertEquals("Quick task", repository.addCalls[0].title)
        assertNotNull(repository.addCalls[0].dueDate)
    }

    // endregion

    // region priority fields preserved in saveTodo

    @Test
    fun `saveTodo preserves all todo fields when adding`() = runTest {
        val todo = Todo(
            id = 0,
            title = "Full task",
            description = "desc",
            priority = Priority.HIGH,
            dueDate = 99999L,
            isCompleted = false
        )

        viewModel.saveTodo(todo)

        val added = repository.addCalls[0]
        assertEquals("Full task", added.title)
        assertEquals("desc", added.description)
        assertEquals(Priority.HIGH, added.priority)
        assertEquals(99999L, added.dueDate)
    }

    // endregion
}
