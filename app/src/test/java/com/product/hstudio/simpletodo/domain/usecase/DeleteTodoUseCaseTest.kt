package com.product.hstudio.simpletodo.domain.usecase

import com.product.hstudio.simpletodo.domain.model.Todo
import com.product.hstudio.simpletodo.fake.FakeTodoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DeleteTodoUseCaseTest {

    private lateinit var repository: FakeTodoRepository
    private lateinit var useCase: DeleteTodoUseCase

    @Before
    fun setUp() {
        repository = FakeTodoRepository()
        useCase = DeleteTodoUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository delete`() = runTest {
        val todo = Todo(id = 1, title = "To Delete")

        useCase(todo)

        assertEquals(1, repository.deleteCalls.size)
        assertEquals(todo, repository.deleteCalls[0])
    }

    @Test
    fun `invoke passes the exact todo to repository`() = runTest {
        val todo = Todo(id = 42, title = "Specific Task")

        useCase(todo)

        assertEquals(42, repository.deleteCalls[0].id)
        assertEquals("Specific Task", repository.deleteCalls[0].title)
    }

    @Test
    fun `repository add and update are never called`() = runTest {
        useCase(Todo(id = 1, title = "Task"))

        assertEquals(0, repository.addCalls.size)
        assertEquals(0, repository.updateCalls.size)
    }
}
