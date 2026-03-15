package com.product.hstudio.simpletodo.ui.todolist.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.product.hstudio.simpletodo.R
import com.product.hstudio.simpletodo.domain.model.Priority
import com.product.hstudio.simpletodo.domain.model.Todo
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTodoDialog(
    todo: Todo?,
    initialDueDate: Long? = null,
    onDismiss: () -> Unit,
    onSave: (Todo) -> Unit
) {
    val existingMillis = todo?.dueDate ?: initialDueDate

    // Store selected date as LocalDate (null = no date)
    var selectedDate by remember {
        mutableStateOf(
            existingMillis?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            }
        )
    }
    // Store selected time as LocalTime (null = no time / midnight not shown)
    var selectedTime by remember {
        mutableStateOf(
            existingMillis?.let {
                val t = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalTime()
                if (t == LocalTime.MIDNIGHT) null else t
            }
        )
    }

    var title by remember { mutableStateOf(todo?.title ?: "") }
    var description by remember { mutableStateOf(todo?.description ?: "") }
    var priority by remember { mutableStateOf(todo?.priority ?: Priority.MEDIUM) }
    var titleError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    if (showDatePicker) {
        val initialUtcMillis = selectedDate
            ?.atStartOfDay(ZoneId.of("UTC"))?.toInstant()?.toEpochMilli()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialUtcMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { utcMillis ->
                        selectedDate = Instant.ofEpochMilli(utcMillis)
                            .atZone(ZoneId.of("UTC")).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedTime?.hour ?: 9,
            initialMinute = selectedTime?.minute ?: 0,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text(stringResource(R.string.set_time)) },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (todo == null) stringResource(R.string.add_todo) else stringResource(R.string.edit_todo)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; titleError = false },
                    label = { Text(stringResource(R.string.hint_title)) },
                    isError = titleError,
                    supportingText = if (titleError) {
                        { Text(stringResource(R.string.error_title_required)) }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.hint_description)) },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                // Date row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(selectedDate?.format(dateFormatter) ?: stringResource(R.string.set_due_date))
                    }
                    if (selectedDate != null) {
                        IconButton(onClick = { selectedDate = null; selectedTime = null }) {
                            Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.cd_clear_date))
                        }
                    }
                }
                // Time row (only when date is set)
                if (selectedDate != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { showTimePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(selectedTime?.format(timeFormatter) ?: stringResource(R.string.set_time_optional))
                        }
                        if (selectedTime != null) {
                            IconButton(onClick = { selectedTime = null }) {
                                Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.cd_clear_time))
                            }
                        }
                    }
                }
                Text(stringResource(R.string.label_priority), style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Priority.entries.forEach { p ->
                        val priorityLabel = when (p) {
                            Priority.LOW -> stringResource(R.string.priority_low)
                            Priority.MEDIUM -> stringResource(R.string.priority_medium)
                            Priority.HIGH -> stringResource(R.string.priority_high)
                        }
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(priorityLabel) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isBlank()) {
                    titleError = true
                    return@TextButton
                }
                val finalDueDate = selectedDate?.let { date ->
                    val time = selectedTime ?: LocalTime.MIDNIGHT
                    date.atTime(time).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                }
                val saved = (todo ?: Todo(title = "")).copy(
                    title = title.trim(),
                    description = description.trim(),
                    priority = priority,
                    dueDate = finalDueDate
                )
                onSave(saved)
            }) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
