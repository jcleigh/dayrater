package com.dayrater.ui.rating

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dayrater.R
import com.dayrater.ui.components.CategoryCard
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Main rating screen composable.
 * Displays categories with emoji selectors for the current date.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(
    modifier: Modifier = Modifier,
    viewModel: RatingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error in snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(RatingEvent.DismissError)
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Date header with navigation
            DateHeader(
                uiState = uiState,
                onPreviousDay = {
                    viewModel.onEvent(RatingEvent.ChangeDate(uiState.date.minusDays(1)))
                },
                onNextDay = {
                    viewModel.onEvent(RatingEvent.ChangeDate(uiState.date.plusDays(1)))
                },
                onGoToToday = {
                    viewModel.onEvent(RatingEvent.GoToToday)
                }
            )
            
            // Family member tabs (if more than one member)
            if (uiState.familyMembers.size > 1) {
                FamilyMemberTabs(
                    familyMembers = uiState.familyMembers,
                    selectedMemberId = uiState.selectedFamilyMemberId,
                    onMemberSelected = { memberId ->
                        viewModel.onEvent(RatingEvent.SelectFamilyMember(memberId))
                    }
                )
            }
            
            // Progress indicator
            if (uiState.categories.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${uiState.ratedCount}/${uiState.totalCategories} rated",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { uiState.progress },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            
            // Content
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.categories.isEmpty() -> {
                    EmptyState()
                }
                else -> {
                    CategoryList(
                        categories = uiState.categories,
                        ratings = uiState.ratings,
                        onRatingSelected = { categoryId, rating ->
                            viewModel.onEvent(RatingEvent.SetRating(categoryId, rating))
                        }
                    )
                }
            }
        }
        
        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun DateHeader(
    uiState: RatingUiState,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onGoToToday: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = if (uiState.isToday) {
                stringResource(R.string.home_title)
            } else {
                uiState.date.format(dateFormatter)
            },
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // Date navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousDay) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_previous_day)
                )
            }
            
            if (uiState.isToday) {
                Text(
                    text = uiState.date.format(dateFormatter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                TextButton(onClick = onGoToToday) {
                    Text(text = stringResource(R.string.calendar_today))
                }
            }
            
            IconButton(
                onClick = onNextDay,
                enabled = !uiState.isToday
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.cd_next_day)
                )
            }
        }
    }
}

@Composable
private fun FamilyMemberTabs(
    familyMembers: List<com.dayrater.domain.model.FamilyMember>,
    selectedMemberId: Long?,
    onMemberSelected: (Long) -> Unit
) {
    val selectedIndex = familyMembers.indexOfFirst { it.id == selectedMemberId }.coerceAtLeast(0)
    
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = Modifier.fillMaxWidth()
    ) {
        familyMembers.forEachIndexed { index, member ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onMemberSelected(member.id) },
                text = { Text(text = member.name) }
            )
        }
    }
}

@Composable
private fun CategoryList(
    categories: List<com.dayrater.domain.model.Category>,
    ratings: Map<Long, com.dayrater.data.local.entity.RatingValue>,
    onRatingSelected: (Long, com.dayrater.data.local.entity.RatingValue) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = categories,
            key = { it.id }
        ) { category ->
            CategoryCard(
                category = category,
                currentRating = ratings[category.id],
                onRatingSelected = { rating ->
                    onRatingSelected(category.id, rating)
                },
                expanded = true
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.home_no_categories),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp)
        )
    }
}
