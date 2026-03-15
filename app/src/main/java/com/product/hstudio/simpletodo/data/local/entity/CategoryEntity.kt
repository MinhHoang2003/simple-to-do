package com.product.hstudio.simpletodo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.product.hstudio.simpletodo.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val color: Int
)

fun CategoryEntity.toDomain() = Category(id = id, name = name, color = color)

fun Category.toEntity() = CategoryEntity(id = id, name = name, color = color)
