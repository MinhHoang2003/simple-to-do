package com.product.hstudio.simpletodo.ui

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.product.hstudio.simpletodo.R
import com.product.hstudio.simpletodo.data.Priority
import com.product.hstudio.simpletodo.data.Todo
import com.product.hstudio.simpletodo.databinding.BottomSheetAddEditTodoBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEditTodoBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddEditTodoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TodoViewModel by activityViewModels()

    private var editingTodo: Todo? = null
    private var selectedDueDate: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddEditTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editingTodo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_TODO, Todo::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_TODO)
        }

        editingTodo?.let { todo ->
            binding.textSheetTitle.text = getString(R.string.edit_todo)
            binding.editTitle.setText(todo.title)
            binding.editDescription.setText(todo.description)
            selectedDueDate = todo.dueDate
            selectedDueDate?.let {
                binding.editDueDate.setText(
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
                )
            }
            when (todo.priority) {
                Priority.LOW -> binding.togglePriority.check(R.id.btnLow)
                Priority.MEDIUM -> binding.togglePriority.check(R.id.btnMedium)
                Priority.HIGH -> binding.togglePriority.check(R.id.btnHigh)
            }
        } ?: binding.togglePriority.check(R.id.btnMedium)

        binding.editDueDate.setOnClickListener { showDatePicker() }
        binding.btnSave.setOnClickListener { saveTodo() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        selectedDueDate?.let { calendar.timeInMillis = it }
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selected = Calendar.getInstance().apply { set(year, month, day) }
                selectedDueDate = selected.timeInMillis
                binding.editDueDate.setText(
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(selected.time)
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveTodo() {
        val title = binding.editTitle.text?.toString()?.trim() ?: ""
        if (title.isEmpty()) {
            binding.editTitle.error = getString(R.string.error_title_required)
            return
        }
        val priority = when (binding.togglePriority.checkedButtonId) {
            R.id.btnLow -> Priority.LOW
            R.id.btnHigh -> Priority.HIGH
            else -> Priority.MEDIUM
        }
        val description = binding.editDescription.text?.toString()?.trim() ?: ""
        val todo = editingTodo?.copy(
            title = title,
            description = description,
            priority = priority,
            dueDate = selectedDueDate
        ) ?: Todo(
            title = title,
            description = description,
            priority = priority,
            dueDate = selectedDueDate
        )
        if (editingTodo != null) viewModel.update(todo) else viewModel.insert(todo)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TODO = "todo"

        fun newInstance(todo: Todo? = null) = AddEditTodoBottomSheet().apply {
            todo?.let {
                arguments = Bundle().apply { putParcelable(ARG_TODO, it) }
            }
        }
    }
}
