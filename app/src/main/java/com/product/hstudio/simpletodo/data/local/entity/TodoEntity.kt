package com.product.hstudio.simpletodo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.product.hstudio.simpletodo.domain.model.Priority
import com.product.hstudio.simpletodo.domain.model.Todo

@Entity(
    tableName = "todos",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index("categoryId")]
)
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: String = Priority.MEDIUM.name,
    val dueDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val categoryId: Int? = null
)

fun TodoEntity.toDomain() = Todo(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    priority = Priority.valueOf(priority),
    dueDate = dueDate,
    createdAt = createdAt,
    categoryId = categoryId
)

fun Todo.toEntity() = TodoEntity(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    priority = priority.name,
    dueDate = dueDate,
    createdAt = createdAt,
    categoryId = categoryId
)
