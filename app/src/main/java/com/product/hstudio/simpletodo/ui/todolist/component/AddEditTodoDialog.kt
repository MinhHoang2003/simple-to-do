package com.product.hstudio.simpletodo.ui.todolist.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.product.hstudio.simpletodo.R
import com.product.hstudio.simpletodo.domain.model.Priority
import com.product.hstudio.simpletodo.domain.model.Todo
import java.time.Instant
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

    var selectedDate by remember {
        mutableStateOf(
            existingMillis?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            }
        )
    }
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

    val save = {
        if (title.isBlank()) {
            titleError = true
        } else {
            val finalDueDate = selectedDate?.let { date ->
                val time = selectedTime ?: LocalTime.MIDNIGHT
                date.atTime(time).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            }
            onSave(
                (todo ?: Todo(title = "")).copy(
                    title = title.trim(),
                    description = description.trim(),
                    priority = priority,
                    dueDate = finalDueDate
                )
            )
        }
    }

    // Date picker
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
        ) { DatePicker(state = datePickerState) }
    }

    // Time picker
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

    // Bottom sheet
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .imePadding()
        ) {
            // Header: screen label + Save button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (todo == null) stringResource(R.string.add_todo)
                    else stringResource(R.string.edit_todo),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Button(
                    onClick = save,
                    enabled = title.isNotBlank()
                ) {
                    Text(stringResource(R.string.save))
                }
            }

            Spacer(Modifier.height(8.dp))

            // Title field — large, no border
            val transparentColors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            )
            TextField(
                value = title,
                onValueChange = { title = it; titleError = false },
                placeholder = {
                    Text(
                        text = stringResource(R.string.hint_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                textStyle = MaterialTheme.typography.titleLarge,
                isError = titleError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = transparentColors
            )
            if (titleError) {
                Text(
                    text = stringResource(R.string.error_title_required),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Description field — no border
            TextField(
                value = description,
                onValueChange = { description = it },
                placeholder = {
                    Text(
                        text = stringResource(R.string.hint_description),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth(),
                colors = transparentColors
            )

            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            // Date row
            ScheduleRow(
                icon = Icons.Default.CalendarToday,
                label = selectedDate?.format(dateFormatter) ?: stringResource(R.string.set_due_date),
                hasValue = selectedDate != null,
                onClick = { showDatePicker = true },
                onClear = { selectedDate = null; selectedTime = null },
                clearDescription = stringResource(R.string.cd_clear_date)
            )

            // Time row — only when date is set
            if (selectedDate != null) {
                Spacer(Modifier.height(4.dp))
                ScheduleRow(
                    icon = Icons.Default.Schedule,
                    label = selectedTime?.format(timeFormatter) ?: stringResource(R.string.set_time_optional),
                    hasValue = selectedTime != null,
                    onClick = { showTimePicker = true },
                    onClear = { selectedTime = null },
                    clearDescription = stringResource(R.string.cd_clear_time)
                )
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            // Priority
            Text(
                text = stringResource(R.string.label_priority),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Priority.entries.forEach { p ->
                    val label = when (p) {
                        Priority.LOW -> stringResource(R.string.priority_low)
                        Priority.MEDIUM -> stringResource(R.string.priority_medium)
                        Priority.HIGH -> stringResource(R.string.priority_high)
                    }
                    val chipColor = when (p) {
                        Priority.HIGH -> MaterialTheme.colorScheme.error
                        Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
                        Priority.LOW -> MaterialTheme.colorScheme.secondary
                    }
                    val isSelected = priority == p
                    FilterChip(
                        selected = isSelected,
                        onClick = { priority = p },
                        label = { Text(label) },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                                    tint = chipColor
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = chipColor.copy(alpha = 0.12f),
                            selectedLabelColor = chipColor
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            selectedBorderColor = chipColor
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleRow(
    icon: ImageVector,
    label: String,
    hasValue: Boolean,
    onClick: () -> Unit,
    onClear: () -> Unit,
    clearDescription: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (hasValue) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (hasValue) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        if (hasValue) {
            IconButton(onClick = onClear, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = clearDescription,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
