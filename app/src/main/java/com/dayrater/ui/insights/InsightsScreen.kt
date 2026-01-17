package com.dayrater.ui.insights

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dayrater.R

/**
 * Insights hub screen - entry point for all insights.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    onNavigateToWeeklySummary: () -> Unit,
    onNavigateToMonthlyCalendar: () -> Unit,
    onNavigateToTrends: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.insights_title)) }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            !uiState.hasEnoughData -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "📊",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Text(
                            text = stringResource(R.string.insights_need_more_data),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                InsightsHubContent(
                    onNavigateToWeeklySummary = onNavigateToWeeklySummary,
                    onNavigateToMonthlyCalendar = onNavigateToMonthlyCalendar,
                    onNavigateToTrends = onNavigateToTrends,
                    onNavigateToStatistics = onNavigateToStatistics,
                    onNavigateToHistory = onNavigateToHistory,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun InsightsHubContent(
    onNavigateToWeeklySummary: () -> Unit,
    onNavigateToMonthlyCalendar: () -> Unit,
    onNavigateToTrends: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        item {
            InsightCard(
                title = stringResource(R.string.insights_weekly),
                description = "See how your week went across all categories",
                icon = Icons.Default.DateRange,
                onClick = onNavigateToWeeklySummary
            )
        }
        
        item {
            InsightCard(
                title = stringResource(R.string.insights_monthly),
                description = "Calendar view with color-coded days",
                icon = Icons.Default.CalendarMonth,
                onClick = onNavigateToMonthlyCalendar
            )
        }
        
        item {
            InsightCard(
                title = stringResource(R.string.insights_trends),
                description = "Line graphs showing rating changes over time",
                icon = Icons.Default.ShowChart,
                onClick = onNavigateToTrends
            )
        }
        
        item {
            InsightCard(
                title = stringResource(R.string.insights_statistics),
                description = "Streaks, averages, and rating distribution",
                icon = Icons.Default.BarChart,
                onClick = onNavigateToStatistics
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        item {
            InsightCard(
                title = stringResource(R.string.insights_view_history),
                description = "Browse your full rating history",
                icon = Icons.Default.History,
                onClick = onNavigateToHistory,
                isSecondary = true
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InsightCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isSecondary: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = if (isSecondary) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
