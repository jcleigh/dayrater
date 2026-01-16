package com.dayrater.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.dayrater.ui.components.CategoryRatingSummary
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Day detail screen composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DayDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL) }
    
    // Show error in snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(DayDetailEvent.DismissError)
        }
    }
    
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (uiState.isToday) {
                            stringResource(R.string.home_title)
                        } else {
                            uiState.date.format(dateFormatter)
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(DayDetailEvent.ToggleEditMode) }) {
                        Icon(
                            imageVector = if (uiState.isEditing) Icons.Default.EditOff else Icons.Default.Edit,
                            contentDescription = stringResource(R.string.history_edit)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Family member tabs (if more than one)
            if (uiState.familyMembers.size > 1) {
                val selectedIndex = uiState.familyMembers.indexOfFirst { 
                    it.id == uiState.selectedFamilyMemberId 
                }.coerceAtLeast(0)
                
                ScrollableTabRow(
                    selectedTabIndex = selectedIndex,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    uiState.familyMembers.forEachIndexed { index, member ->
                        Tab(
                            selected = index == selectedIndex,
                            onClick = { 
                                viewModel.onEvent(DayDetailEvent.SelectFamilyMember(member.id))
                            },
                            text = { Text(text = member.name) }
                        )
                    }
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
                !uiState.hasRatings && !uiState.isEditing -> {
                    EmptyHistoryState()
                }
                else -> {
                    RatingsList(
                        uiState = uiState,
                        onRatingSelected = { categoryId, rating ->
                            viewModel.onEvent(DayDetailEvent.SetRating(categoryId, rating))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingsList(
    uiState: DayDetailUiState,
    onRatingSelected: (Long, com.dayrater.data.local.entity.RatingValue) -> Unit
) {
    val ratingsMap = uiState.dayRatings?.ratings?.associate { 
        it.categoryId to it.value 
    } ?: emptyMap()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = uiState.categories,
            key = { it.id }
        ) { category ->
            val rating = ratingsMap[category.id]
            
            if (uiState.isEditing) {
                CategoryCard(
                    category = category,
                    currentRating = rating,
                    onRatingSelected = { newRating ->
                        onRatingSelected(category.id, newRating)
                    },
                    expanded = true
                )
            } else {
                CategoryRatingSummary(
                    category = category,
                    rating = rating
                )
            }
        }
    }
}

@Composable
private fun EmptyHistoryState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "📭",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.history_no_ratings),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
