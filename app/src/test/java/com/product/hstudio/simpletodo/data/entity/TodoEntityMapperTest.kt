package com.product.hstudio.simpletodo.data.entity

import com.product.hstudio.simpletodo.data.local.entity.TodoEntity
import com.product.hstudio.simpletodo.data.local.entity.toDomain
import com.product.hstudio.simpletodo.data.local.entity.toEntity
import com.product.hstudio.simpletodo.domain.model.Priority
import com.product.hstudio.simpletodo.domain.model.Todo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TodoEntityMapperTest {

    // region toDomain

    @Test
    fun `toDomain maps all fields correctly`() {
        val entity = TodoEntity(
            id = 1,
            title = "Test",
            description = "Desc",
            isCompleted = true,
            priority = "HIGH",
            dueDate = 1000L,
            createdAt = 2000L
        )

        val domain = entity.toDomain()

        assertEquals(1, domain.id)
        assertEquals("Test", domain.title)
        assertEquals("Desc", domain.description)
        assertTrue(domain.isCompleted)
        assertEquals(Priority.HIGH, domain.priority)
        assertEquals(1000L, domain.dueDate)
        assertEquals(2000L, domain.createdAt)
    }

    @Test
    fun `toDomain maps LOW priority correctly`() {
        val entity = TodoEntity(id = 1, title = "T", priority = "LOW")
        assertEquals(Priority.LOW, entity.toDomain().priority)
    }

    @Test
    fun `toDomain maps MEDIUM priority correctly`() {
        val entity = TodoEntity(id = 1, title = "T", priority = "MEDIUM")
        assertEquals(Priority.MEDIUM, entity.toDomain().priority)
    }

    @Test
    fun `toDomain handles null dueDate`() {
        val entity = TodoEntity(id = 1, title = "No date", dueDate = null)
        assertNull(entity.toDomain().dueDate)
    }

    @Test
    fun `toDomain maps isCompleted false correctly`() {
        val entity = TodoEntity(id = 1, title = "T", isCompleted = false)
        assertFalse(entity.toDomain().isCompleted)
    }

    // endregion

    // region toEntity

    @Test
    fun `toEntity maps all fields correctly`() {
        val todo = Todo(
            id = 1,
            title = "Test",
            description = "Desc",
            isCompleted = true,
            priority = Priority.HIGH,
            dueDate = 1000L,
            createdAt = 2000L
        )

        val entity = todo.toEntity()

        assertEquals(1, entity.id)
        assertEquals("Test", entity.title)
        assertEquals("Desc", entity.description)
        assertTrue(entity.isCompleted)
        assertEquals("HIGH", entity.priority)
        assertEquals(1000L, entity.dueDate)
        assertEquals(2000L, entity.createdAt)
    }

    @Test
    fun `toEntity stores priority as name string`() {
        assertEquals("LOW", Todo(title = "x", priority = Priority.LOW).toEntity().priority)
        assertEquals("MEDIUM", Todo(title = "x", priority = Priority.MEDIUM).toEntity().priority)
        assertEquals("HIGH", Todo(title = "x", priority = Priority.HIGH).toEntity().priority)
    }

    @Test
    fun `toEntity handles null dueDate`() {
        val entity = Todo(title = "No date", dueDate = null).toEntity()
        assertNull(entity.dueDate)
    }

    // endregion

    // region roundtrip

    @Test
    fun `roundtrip toEntity then toDomain preserves all fields`() {
        val original = Todo(
            id = 42,
            title = "Roundtrip",
            description = "Check",
            isCompleted = false,
            priority = Priority.MEDIUM,
            dueDate = 99999L,
            createdAt = 11111L
        )

        val result = original.toEntity().toDomain()

        assertEquals(original, result)
    }

    @Test
    fun `roundtrip with null dueDate preserves null`() {
        val original = Todo(id = 1, title = "No date", dueDate = null, createdAt = 500L)
        val result = original.toEntity().toDomain()
        assertEquals(original, result)
    }

    @Test
    fun `roundtrip preserves all three priority values`() {
        Priority.entries.forEach { priority ->
            val original = Todo(id = 1, title = "T", priority = priority, createdAt = 0L)
            assertEquals(priority, original.toEntity().toDomain().priority)
        }
    }

    // endregion
}
