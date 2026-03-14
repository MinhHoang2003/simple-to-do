package com.product.hstudio.simpletodo.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.product.hstudio.simpletodo.R
import com.product.hstudio.simpletodo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TodoViewModel by viewModels()
    private lateinit var adapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupRecyclerView()

        viewModel.allTodos.observe(this) { todos ->
            adapter.submitList(todos)
            binding.emptyView.visibility = if (todos.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fab.setOnClickListener {
            AddEditTodoBottomSheet.newInstance().show(supportFragmentManager, "add_todo")
        }
    }

    private fun setupRecyclerView() {
        adapter = TodoAdapter(
            onToggleComplete = { viewModel.toggleComplete(it) },
            onEdit = { todo ->
                AddEditTodoBottomSheet.newInstance(todo).show(supportFragmentManager, "edit_todo")
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val todo = adapter.getTodoAt(viewHolder.adapterPosition)
                viewModel.delete(todo)
                Snackbar.make(binding.root, R.string.todo_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) { viewModel.insert(todo) }
                    .show()
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.recyclerView)
    }
}
