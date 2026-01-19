package com.dayrater.ui.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dayrater.R
import com.dayrater.domain.model.UserStatistics
import com.dayrater.ui.insights.components.DistributionBar
import com.dayrater.ui.insights.components.StatCard
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

/**
 * Screen displaying user statistics.
 */
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
                title = { Text(stringResource(R.string.stats_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                }
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
            uiState.statistics == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.insights_no_data),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                StatisticsContent(
                    statistics = uiState.statistics!!,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun StatisticsContent(
    statistics: UserStatistics,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Streak cards
        Text(
            text = "Streaks",
            style = MaterialTheme.typography.titleMedium
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                title = stringResource(R.string.stats_current_streak),
                value = "${statistics.currentStreak}",
                subtitle = if (statistics.currentStreak == 1) "day" else "days",
                modifier = Modifier.weight(1f)
            )
            
            StatCard(
                title = stringResource(R.string.stats_longest_streak),
                value = "${statistics.longestStreak}",
                subtitle = if (statistics.longestStreak == 1) "day" else "days",
                modifier = Modifier.weight(1f)
            )
        }
        
        // Total days
        StatCard(
            title = stringResource(R.string.stats_total_days),
            value = "${statistics.totalDaysRated}",
            subtitle = statistics.firstRatingDate?.let { 
                val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                "Since ${it.format(formatter)}"
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Rating distribution
        Text(
            text = stringResource(R.string.stats_distribution),
            style = MaterialTheme.typography.titleMedium
        )
        
        Card(modifier = Modifier.fillMaxWidth()) {
            DistributionBar(
                distribution = statistics.overallDistribution,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        // Day of week analysis
        if (statistics.dayOfWeekAverages.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stringResource(R.string.stats_day_of_week),
                style = MaterialTheme.typography.titleMedium
            )
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val bestDay = statistics.dayOfWeekAverages.maxByOrNull { it.value }
                    val worstDay = statistics.dayOfWeekAverages.minByOrNull { it.value }
                    
                    bestDay?.let { (day, _) ->
                        Text(
                            text = stringResource(
                                R.string.stats_best_day,
                                day.getDisplayName(TextStyle.FULL, Locale.getDefault())
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    worstDay?.let { (day, _) ->
                        Text(
                            text = stringResource(
                                R.string.stats_worst_day,
                                day.getDisplayName(TextStyle.FULL, Locale.getDefault())
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        
        // Category averages
        if (statistics.categoryAverages.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stringResource(R.string.stats_category_averages),
                style = MaterialTheme.typography.titleMedium
            )
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    statistics.categoryAverages.values
                        .sortedByDescending { it.averageScore }
                        .forEach { categoryAvg ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = categoryAvg.categoryName,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = getEmojiForScore(categoryAvg.averageScore),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun getEmojiForScore(score: Float): String {
    return when {
        score >= 2.5f -> "😊"
        score >= 1.5f -> "😐"
        else -> "😢"
    }
}
