package com.product.hstudio.simpletodo.data.repository

import com.product.hstudio.simpletodo.data.local.entity.TodoEntity
import com.product.hstudio.simpletodo.domain.model.Priority
import com.product.hstudio.simpletodo.domain.model.Todo
import com.product.hstudio.simpletodo.fake.FakeTodoDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TodoRepositoryImplTest {

    private lateinit var dao: FakeTodoDao
    private lateinit var repository: TodoRepositoryImpl

    @Before
    fun setUp() {
        dao = FakeTodoDao()
        repository = TodoRepositoryImpl(dao)
    }

    // region getTodos

    @Test
    fun `getTodos returns empty list when dao is empty`() = runTest {
        dao.emit(emptyList())

        val result = repository.getTodos().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getTodos maps entity fields to domain correctly`() = runTest {
        dao.emit(listOf(
            TodoEntity(
                id = 1,
                title = "Test",
                description = "Desc",
                isCompleted = true,
                priority = "HIGH",
                dueDate = 1000L,
                createdAt = 2000L
            )
        ))

        val result = repository.getTodos().first()

        assertEquals(1, result.size)
        val todo = result[0]
        assertEquals(1, todo.id)
        assertEquals("Test", todo.title)
        assertEquals("Desc", todo.description)
        assertEquals(true, todo.isCompleted)
        assertEquals(Priority.HIGH, todo.priority)
        assertEquals(1000L, todo.dueDate)
        assertEquals(2000L, todo.createdAt)
    }

    @Test
    fun `getTodos maps multiple entities`() = runTest {
        dao.emit(listOf(
            TodoEntity(id = 1, title = "First", priority = "LOW"),
            TodoEntity(id = 2, title = "Second", priority = "HIGH")
        ))

        val result = repository.getTodos().first()

        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals(2, result[1].id)
    }

    @Test
    fun `getTodos maps null dueDate correctly`() = runTest {
        dao.emit(listOf(TodoEntity(id = 1, title = "No date", dueDate = null)))

        val result = repository.getTodos().first()

        assertNull(result[0].dueDate)
    }

    @Test
    fun `getTodos reflects dao updates`() = runTest {
        dao.emit(listOf(TodoEntity(id = 1, title = "Before")))
        assertEquals("Before", repository.getTodos().first()[0].title)

        dao.emit(listOf(TodoEntity(id = 1, title = "After")))
        assertEquals("After", repository.getTodos().first()[0].title)
    }

    // endregion

    // region add

    @Test
    fun `add calls dao insert with correct entity`() = runTest {
        val todo = Todo(id = 0, title = "New Task", priority = Priority.LOW, createdAt = 500L)

        repository.add(todo)

        assertEquals(1, dao.insertCalls.size)
        val inserted = dao.insertCalls[0]
        assertEquals("New Task", inserted.title)
        assertEquals("LOW", inserted.priority)
        assertEquals(500L, inserted.createdAt)
    }

    @Test
    fun `add maps null dueDate to entity`() = runTest {
        repository.add(Todo(title = "No date", dueDate = null))

        assertNull(dao.insertCalls[0].dueDate)
    }

    @Test
    fun `add does not call update or delete`() = runTest {
        repository.add(Todo(title = "Task"))

        assertTrue(dao.updateCalls.isEmpty())
        assertTrue(dao.deleteCalls.isEmpty())
    }

    // endregion

    // region update

    @Test
    fun `update calls dao update with correct entity`() = runTest {
        val todo = Todo(id = 2, title = "Updated", isCompleted = true, priority = Priority.MEDIUM)

        repository.update(todo)

        assertEquals(1, dao.updateCalls.size)
        val updated = dao.updateCalls[0]
        assertEquals(2, updated.id)
        assertEquals("Updated", updated.title)
        assertEquals(true, updated.isCompleted)
        assertEquals("MEDIUM", updated.priority)
    }

    @Test
    fun `update does not call insert or delete`() = runTest {
        repository.update(Todo(id = 1, title = "Task"))

        assertTrue(dao.insertCalls.isEmpty())
        assertTrue(dao.deleteCalls.isEmpty())
    }

    // endregion

    // region delete

    @Test
    fun `delete calls dao delete with correct entity`() = runTest {
        val todo = Todo(id = 3, title = "To Delete")

        repository.delete(todo)

        assertEquals(1, dao.deleteCalls.size)
        assertEquals(3, dao.deleteCalls[0].id)
        assertEquals("To Delete", dao.deleteCalls[0].title)
    }

    @Test
    fun `delete does not call insert or update`() = runTest {
        repository.delete(Todo(id = 1, title = "Task"))

        assertTrue(dao.insertCalls.isEmpty())
        assertTrue(dao.updateCalls.isEmpty())
    }

    // endregion
}
