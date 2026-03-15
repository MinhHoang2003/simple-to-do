package com.product.hstudio.simpletodo.di

import android.content.Context
import com.product.hstudio.simpletodo.data.local.dao.CategoryDao
import com.product.hstudio.simpletodo.data.local.dao.TodoDao
import com.product.hstudio.simpletodo.data.local.database.TodoDatabase
import com.product.hstudio.simpletodo.data.repository.CategoryRepositoryImpl
import com.product.hstudio.simpletodo.data.repository.TodoRepositoryImpl
import com.product.hstudio.simpletodo.domain.repository.CategoryRepository
import com.product.hstudio.simpletodo.domain.repository.TodoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTodoDatabase(@ApplicationContext context: Context): TodoDatabase =
        TodoDatabase.getDatabase(context)

    @Provides
    @Singleton
    fun provideTodoDao(database: TodoDatabase): TodoDao = database.todoDao()

    @Provides
    @Singleton
    fun provideTodoRepository(dao: TodoDao): TodoRepository = TodoRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideCategoryDao(db: TodoDatabase): CategoryDao = db.categoryDao()

    @Provides
    @Singleton
    fun provideCategoryRepository(dao: CategoryDao): CategoryRepository = CategoryRepositoryImpl(dao)
}
