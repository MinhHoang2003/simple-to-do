package com.product.hstudio.simpletodo.fake

import com.product.hstudio.simpletodo.data.local.dao.TodoDao
import com.product.hstudio.simpletodo.data.local.entity.TodoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeTodoDao : TodoDao {

    private val _entities = MutableStateFlow<List<TodoEntity>>(emptyList())

    val insertCalls = mutableListOf<TodoEntity>()
    val updateCalls = mutableListOf<TodoEntity>()
    val deleteCalls = mutableListOf<TodoEntity>()

    fun emit(entities: List<TodoEntity>) {
        _entities.value = entities
    }

    override fun getAllTodos(): Flow<List<TodoEntity>> = _entities

    override suspend fun insert(entity: TodoEntity) {
        insertCalls.add(entity)
    }

    override suspend fun update(entity: TodoEntity) {
        updateCalls.add(entity)
    }

    override suspend fun delete(entity: TodoEntity) {
        deleteCalls.add(entity)
    }
}
