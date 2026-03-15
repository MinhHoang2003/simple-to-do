package com.product.hstudio.simpletodo.domain.usecase

import com.product.hstudio.simpletodo.domain.model.Priority
import com.product.hstudio.simpletodo.domain.model.Todo
import com.product.hstudio.simpletodo.fake.FakeTodoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ToggleTodoUseCaseTest {

    private lateinit var repository: FakeTodoRepository
    private lateinit var useCase: ToggleTodoUseCase

    @Before
    fun setUp() {
        repository = FakeTodoRepository()
        useCase = ToggleTodoUseCase(repository)
    }

    @Test
    fun `incomplete todo is marked completed`() = runTest {
        val todo = Todo(id = 1, title = "Task", isCompleted = false)

        useCase(todo)

        assertTrue(repository.updateCalls[0].isCompleted)
    }

    @Test
    fun `completed todo is marked incomplete`() = runTest {
        val todo = Todo(id = 1, title = "Task", isCompleted = true)

        useCase(todo)

        assertFalse(repository.updateCalls[0].isCompleted)
    }

    @Test
    fun `all other fields are preserved after toggle`() = runTest {
        val todo = Todo(
            id = 5,
            title = "Keep Me",
            description = "desc",
            priority = Priority.HIGH,
            dueDate = 12345L,
            createdAt = 67890L,
            isCompleted = false
        )

        useCase(todo)

        val updated = repository.updateCalls[0]
        assertEquals(5, updated.id)
        assertEquals("Keep Me", updated.title)
        assertEquals("desc", updated.description)
        assertEquals(Priority.HIGH, updated.priority)
        assertEquals(12345L, updated.dueDate)
        assertEquals(67890L, updated.createdAt)
        assertTrue(updated.isCompleted)
    }

    @Test
    fun `delegates to repository update not add or delete`() = runTest {
        useCase(Todo(id = 1, title = "Task", isCompleted = false))

        assertEquals(1, repository.updateCalls.size)
        assertEquals(0, repository.addCalls.size)
        assertEquals(0, repository.deleteCalls.size)
    }

    @Test
    fun `toggling twice restores original state`() = runTest {
        val todo = Todo(id = 1, title = "Task", isCompleted = false)

        useCase(todo)
        val afterFirst = repository.updateCalls[0]
        assertTrue(afterFirst.isCompleted)

        useCase(afterFirst)
        val afterSecond = repository.updateCalls[1]
        assertFalse(afterSecond.isCompleted)
    }
}
