package com.product.hstudio.simpletodo.domain.usecase

import com.product.hstudio.simpletodo.domain.model.Priority
import com.product.hstudio.simpletodo.domain.model.Todo
import com.product.hstudio.simpletodo.fake.FakeTodoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UpdateTodoUseCaseTest {

    private lateinit var repository: FakeTodoRepository
    private lateinit var useCase: UpdateTodoUseCase

    @Before
    fun setUp() {
        repository = FakeTodoRepository()
        useCase = UpdateTodoUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository update`() = runTest {
        val todo = Todo(id = 1, title = "Updated Task")

        useCase(todo)

        assertEquals(1, repository.updateCalls.size)
        assertEquals(todo, repository.updateCalls[0])
    }

    @Test
    fun `invoke passes updated fields intact`() = runTest {
        val todo = Todo(
            id = 3,
            title = "Edited",
            description = "New desc",
            isCompleted = true,
            priority = Priority.LOW,
            dueDate = null
        )

        useCase(todo)

        val updated = repository.updateCalls[0]
        assertEquals(3, updated.id)
        assertEquals("Edited", updated.title)
        assertEquals("New desc", updated.description)
        assertEquals(true, updated.isCompleted)
        assertEquals(Priority.LOW, updated.priority)
        assertEquals(null, updated.dueDate)
    }

    @Test
    fun `repository add is never called`() = runTest {
        useCase(Todo(id = 1, title = "Task"))

        assertEquals(0, repository.addCalls.size)
    }
}
