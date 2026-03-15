package com.product.hstudio.simpletodo.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.product.hstudio.simpletodo.data.local.dao.CategoryDao
import com.product.hstudio.simpletodo.data.local.dao.TodoDao
import com.product.hstudio.simpletodo.data.local.entity.CategoryEntity
import com.product.hstudio.simpletodo.data.local.entity.TodoEntity

@Database(entities = [TodoEntity::class, CategoryEntity::class], version = 2)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: TodoDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS categories (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, color INTEGER NOT NULL)")
                db.execSQL("ALTER TABLE todos ADD COLUMN categoryId INTEGER REFERENCES categories(id) ON DELETE SET NULL")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_todos_categoryId ON todos(categoryId)")
            }
        }

        fun getDatabase(context: Context): TodoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "todo_database"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
