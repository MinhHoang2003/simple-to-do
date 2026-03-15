package com.product.hstudio.simpletodo.domain.usecase

import com.product.hstudio.simpletodo.domain.model.Priority
import com.product.hstudio.simpletodo.domain.model.Todo
import com.product.hstudio.simpletodo.fake.FakeTodoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AddTodoUseCaseTest {

    private lateinit var repository: FakeTodoRepository
    private lateinit var useCase: AddTodoUseCase

    @Before
    fun setUp() {
        repository = FakeTodoRepository()
        useCase = AddTodoUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository add`() = runTest {
        val todo = Todo(id = 1, title = "New Task")

        useCase(todo)

        assertEquals(1, repository.addCalls.size)
        assertEquals(todo, repository.addCalls[0])
    }

    @Test
    fun `invoke passes todo with all fields intact`() = runTest {
        val todo = Todo(
            id = 7,
            title = "Task",
            description = "Some description",
            isCompleted = false,
            priority = Priority.HIGH,
            dueDate = 123456L,
            createdAt = 654321L
        )

        useCase(todo)

        val added = repository.addCalls[0]
        assertEquals(7, added.id)
        assertEquals("Task", added.title)
        assertEquals("Some description", added.description)
        assertEquals(Priority.HIGH, added.priority)
        assertEquals(123456L, added.dueDate)
        assertEquals(654321L, added.createdAt)
    }

    @Test
    fun `invoke called multiple times adds each todo`() = runTest {
        val first = Todo(id = 1, title = "First")
        val second = Todo(id = 2, title = "Second")

        useCase(first)
        useCase(second)

        assertEquals(2, repository.addCalls.size)
        assertEquals(first, repository.addCalls[0])
        assertEquals(second, repository.addCalls[1])
    }
}
