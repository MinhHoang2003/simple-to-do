package com.product.hstudio.simpletodo.domain.usecase

import com.product.hstudio.simpletodo.domain.model.Priority
import com.product.hstudio.simpletodo.domain.model.Todo
import com.product.hstudio.simpletodo.fake.FakeTodoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetTodosUseCaseTest {

    private lateinit var repository: FakeTodoRepository
    private lateinit var useCase: GetTodosUseCase

    @Before
    fun setUp() {
        repository = FakeTodoRepository()
        useCase = GetTodosUseCase(repository)
    }

    @Test
    fun `empty list returns empty`() = runTest {
        repository.emit(emptyList())

        val result = useCase().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `incomplete todos appear before completed`() = runTest {
        val completed = Todo(id = 1, title = "Done", isCompleted = true)
        val incomplete = Todo(id = 2, title = "Not done", isCompleted = false)
        repository.emit(listOf(completed, incomplete))

        val result = useCase().first()

        assertFalse(result[0].isCompleted)
        assertTrue(result[1].isCompleted)
    }

    @Test
    fun `incomplete todos sorted by priority HIGH then MEDIUM then LOW`() = runTest {
        val low = Todo(id = 1, title = "Low", priority = Priority.LOW)
        val high = Todo(id = 2, title = "High", priority = Priority.HIGH)
        val medium = Todo(id = 3, title = "Medium", priority = Priority.MEDIUM)
        repository.emit(listOf(low, medium, high))

        val result = useCase().first()

        assertEquals(Priority.HIGH, result[0].priority)
        assertEquals(Priority.MEDIUM, result[1].priority)
        assertEquals(Priority.LOW, result[2].priority)
    }

    @Test
    fun `same priority sorted by createdAt descending — newer first`() = runTest {
        val older = Todo(id = 1, title = "Older", priority = Priority.MEDIUM, createdAt = 1000L)
        val newer = Todo(id = 2, title = "Newer", priority = Priority.MEDIUM, createdAt = 2000L)
        repository.emit(listOf(older, newer))

        val result = useCase().first()

        assertEquals(2, result[0].id)
        assertEquals(1, result[1].id)
    }

    @Test
    fun `completed todos sorted among themselves by priority then createdAt`() = runTest {
        val completedHigh = Todo(id = 1, title = "CH", isCompleted = true, priority = Priority.HIGH, createdAt = 1000L)
        val completedLow = Todo(id = 2, title = "CL", isCompleted = true, priority = Priority.LOW, createdAt = 2000L)
        repository.emit(listOf(completedLow, completedHigh))

        val result = useCase().first()

        assertEquals(1, result[0].id) // HIGH before LOW
        assertEquals(2, result[1].id)
    }

    @Test
    fun `incomplete always before completed regardless of priority`() = runTest {
        val completedHigh = Todo(id = 1, title = "CompHigh", isCompleted = true, priority = Priority.HIGH)
        val incompleteLow = Todo(id = 2, title = "IncLow", isCompleted = false, priority = Priority.LOW)
        repository.emit(listOf(completedHigh, incompleteLow))

        val result = useCase().first()

        assertEquals(2, result[0].id) // incomplete LOW before completed HIGH
        assertEquals(1, result[1].id)
    }

    @Test
    fun `single todo returned as-is`() = runTest {
        val todo = Todo(id = 1, title = "Only", priority = Priority.HIGH)
        repository.emit(listOf(todo))

        val result = useCase().first()

        assertEquals(1, result.size)
        assertEquals(1, result[0].id)
    }

    @Test
    fun `flow emits new sorted list when repository updates`() = runTest {
        val flow = useCase()
        repository.emit(listOf(Todo(id = 1, title = "A", priority = Priority.LOW)))
        assertEquals(Priority.LOW, flow.first()[0].priority)

        repository.emit(listOf(
            Todo(id = 1, title = "A", priority = Priority.LOW),
            Todo(id = 2, title = "B", priority = Priority.HIGH)
        ))
        assertEquals(Priority.HIGH, flow.first()[0].priority)
    }
}
