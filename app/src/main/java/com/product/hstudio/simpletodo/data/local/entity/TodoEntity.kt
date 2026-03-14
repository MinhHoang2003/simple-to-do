package com.product.hstudio.simpletodo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.product.hstudio.simpletodo.domain.model.Priority
import com.product.hstudio.simpletodo.domain.model.Todo

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: String = Priority.MEDIUM.name,
    val dueDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

fun TodoEntity.toDomain() = Todo(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    priority = Priority.valueOf(priority),
    dueDate = dueDate,
    createdAt = createdAt
)

fun Todo.toEntity() = TodoEntity(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    priority = priority.name,
    dueDate = dueDate,
    createdAt = createdAt
)
