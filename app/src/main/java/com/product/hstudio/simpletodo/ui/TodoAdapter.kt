package com.product.hstudio.simpletodo.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.product.hstudio.simpletodo.R
import com.product.hstudio.simpletodo.data.Priority
import com.product.hstudio.simpletodo.data.Todo
import com.product.hstudio.simpletodo.databinding.ItemTodoBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TodoAdapter(
    private val onToggleComplete: (Todo) -> Unit,
    private val onEdit: (Todo) -> Unit
) : ListAdapter<Todo, TodoAdapter.TodoViewHolder>(DiffCallback()) {

    inner class TodoViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(todo: Todo) {
            binding.checkboxComplete.setOnCheckedChangeListener(null)
            binding.checkboxComplete.isChecked = todo.isCompleted

            binding.textTitle.text = todo.title
            if (todo.isCompleted) {
                binding.textTitle.paintFlags =
                    binding.textTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.textTitle.alpha = 0.5f
            } else {
                binding.textTitle.paintFlags =
                    binding.textTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.textTitle.alpha = 1f
            }

            if (todo.description.isNotEmpty()) {
                binding.textDescription.text = todo.description
                binding.textDescription.visibility = android.view.View.VISIBLE
            } else {
                binding.textDescription.visibility = android.view.View.GONE
            }

            if (todo.dueDate != null) {
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                binding.textDueDate.text = sdf.format(Date(todo.dueDate))
                binding.textDueDate.visibility = android.view.View.VISIBLE
            } else {
                binding.textDueDate.visibility = android.view.View.GONE
            }

            val priorityColor = when (todo.priority) {
                Priority.LOW -> ContextCompat.getColor(binding.root.context, R.color.priority_low)
                Priority.MEDIUM -> ContextCompat.getColor(binding.root.context, R.color.priority_medium)
                Priority.HIGH -> ContextCompat.getColor(binding.root.context, R.color.priority_high)
            }
            binding.priorityIndicator.setBackgroundColor(priorityColor)

            binding.checkboxComplete.setOnCheckedChangeListener { _, _ -> onToggleComplete(todo) }
            binding.root.setOnClickListener { onEdit(todo) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getTodoAt(position: Int): Todo = getItem(position)

    class DiffCallback : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Todo, newItem: Todo) = oldItem == newItem
    }
}
