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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dayrater.R
import com.dayrater.data.local.entity.RatingValue
import com.dayrater.domain.model.DayIndicator
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

/**
 * Screen showing daily breakdown for a category in a specific week.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryWeekDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: CategoryWeekDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    val title = stringResource(
        R.string.weekly_category_detail_title,
        uiState.categoryName,
        uiState.weekStart.format(dateFormatter)
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.categoryName) },
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
            else -> {
                CategoryWeekDetailContent(
                    dailyBreakdown = uiState.dailyBreakdown,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun CategoryWeekDetailContent(
    dailyBreakdown: List<DayIndicator>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        items(dailyBreakdown) { dayIndicator ->
            DayRatingCard(dayIndicator = dayIndicator)
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DayRatingCard(
    dayIndicator: DayIndicator
) {
    val dayName = dayIndicator.date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    val formattedDate = dayIndicator.date.format(dateFormatter)
    
    val (backgroundColor, emoji) = when (dayIndicator.overallRating) {
        RatingValue.POSITIVE -> Color(0xFF4CAF50).copy(alpha = 0.1f) to RatingValue.POSITIVE.emoji
        RatingValue.NEUTRAL -> Color(0xFFFFEB3B).copy(alpha = 0.1f) to RatingValue.NEUTRAL.emoji
        RatingValue.NEGATIVE -> Color(0xFFF44336).copy(alpha = 0.1f) to RatingValue.NEGATIVE.emoji
        null -> MaterialTheme.colorScheme.surfaceVariant to "—"
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.size(48.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
