package com.dayrater.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dayrater.data.local.entity.RatingValue
import com.dayrater.domain.model.Category

/**
 * Card component for displaying a category with its rating selector.
 * 
 * @param category The category to display
 * @param currentRating The current rating for this category, or null if not rated
 * @param onRatingSelected Callback when a rating is selected
 * @param modifier Modifier for the component
 * @param expanded Whether the rating selector is expanded (shown)
 * @param onExpandedChange Callback when expansion state should change
 */
@Composable
fun CategoryCard(
    category: Category,
    currentRating: RatingValue?,
    onRatingSelected: (RatingValue) -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    onExpandedChange: ((Boolean) -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        onClick = { onExpandedChange?.invoke(!expanded) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row with category name and current rating indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (currentRating != null) {
                    EmojiRatingIndicator(rating = currentRating)
                }
            }
            
            // Expandable rating selector
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    EmojiRatingSelector(
                        selectedRating = currentRating,
                        onRatingSelected = onRatingSelected
                    )
                }
            }
        }
    }
}

/**
 * Compact category card for history/summary views.
 * Shows category name with its rating in a single row.
 * 
 * @param category The category to display
 * @param rating The rating for this category, or null if not rated
 * @param modifier Modifier for the component
 */
@Composable
fun CategoryRatingSummary(
    category: Category,
    rating: RatingValue?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        if (rating != null) {
            EmojiRatingDisplay(rating = rating)
        } else {
            Text(
                text = "Not rated",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Interactive category card that manages its own expanded state.
 * Useful for lists where each card can be independently expanded.
 * 
 * @param category The category to display
 * @param currentRating The current rating for this category
 * @param onRatingSelected Callback when a rating is selected
 * @param modifier Modifier for the component
 * @param initiallyExpanded Whether the card starts expanded
 */
@Composable
fun ExpandableCategoryCard(
    category: Category,
    currentRating: RatingValue?,
    onRatingSelected: (RatingValue) -> Unit,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    
    CategoryCard(
        category = category,
        currentRating = currentRating,
        onRatingSelected = onRatingSelected,
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it }
    )
}
