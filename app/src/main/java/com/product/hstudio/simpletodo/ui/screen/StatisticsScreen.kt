package com.product.hstudio.simpletodo.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.product.hstudio.simpletodo.R
import com.product.hstudio.simpletodo.ui.DailyStat
import com.product.hstudio.simpletodo.ui.StatisticsUiState
import com.product.hstudio.simpletodo.ui.StatisticsViewModel
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.stat_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { MonthNavigator(uiState, viewModel::previousMonth, viewModel::nextMonth) }

            item { SummaryRow(uiState) }

            item { PrioritySection(uiState) }

            item {
                Text(
                    text = stringResource(R.string.stat_daily_breakdown),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (uiState.totalTasks == 0) {
                item {
                    Text(
                        text = stringResource(R.string.stat_no_tasks_month),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(uiState.dailyStats.filter { it.total > 0 }) { stat ->
                    DayStatRow(stat)
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun MonthNavigator(
    uiState: StatisticsUiState,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val monthName = uiState.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val year = uiState.yearMonth.year
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.cd_previous_month)
            )
        }
        Text(
            text = "$monthName $year",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNext) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.cd_next_month)
            )
        }
    }
}

@Composable
private fun SummaryRow(uiState: StatisticsUiState) {
    val rate = if (uiState.totalTasks > 0) (uiState.completedTasks * 100) / uiState.totalTasks else 0
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryCard(
            label = stringResource(R.string.stat_total_tasks),
            value = uiState.totalTasks.toString(),
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            label = stringResource(R.string.stat_completed),
            value = uiState.completedTasks.toString(),
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            label = stringResource(R.string.stat_completion_rate),
            value = stringResource(R.string.stat_rate_format, rate),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PrioritySection(uiState: StatisticsUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.stat_priority_breakdown),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        PriorityRow(
            label = stringResource(R.string.priority_high),
            count = uiState.highCount,
            total = uiState.totalTasks,
            color = MaterialTheme.colorScheme.error
        )
        PriorityRow(
            label = stringResource(R.string.priority_medium),
            count = uiState.mediumCount,
            total = uiState.totalTasks,
            color = MaterialTheme.colorScheme.tertiary
        )
        PriorityRow(
            label = stringResource(R.string.priority_low),
            count = uiState.lowCount,
            total = uiState.totalTasks,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun PriorityRow(label: String, count: Int, total: Int, color: Color) {
    val progress = if (total > 0) count.toFloat() / total else 0f
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(60.dp)
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.weight(1f),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(24.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun DayStatRow(stat: DailyStat) {
    val progress = if (stat.total > 0) stat.completed.toFloat() / stat.total else 0f
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stat.day.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(24.dp),
            textAlign = TextAlign.End
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = stringResource(R.string.progress_label, stat.completed, stat.total),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(56.dp),
            textAlign = TextAlign.End
        )
    }
}
