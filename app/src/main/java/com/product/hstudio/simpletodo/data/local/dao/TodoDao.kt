package com.product.hstudio.simpletodo.data.local.dao

import androidx.room.*
import com.product.hstudio.simpletodo.data.local.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    fun getAllTodos(): Flow<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TodoEntity)

    @Update
    suspend fun update(entity: TodoEntity)

    @Delete
    suspend fun delete(entity: TodoEntity)
}
