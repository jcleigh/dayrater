package com.dayrater.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dayrater.data.local.entity.RatingValue
import com.dayrater.ui.theme.EmojiLargeStyle
import com.dayrater.ui.theme.EmojiStyle
import com.dayrater.ui.theme.RatingColors

/**
 * Emoji rating selector component.
 * Displays all 5 emoji ratings in a row for user selection.
 * 
 * @param selectedRating The currently selected rating, or null if none selected
 * @param onRatingSelected Callback when a rating is selected
 * @param modifier Modifier for the component
 * @param enabled Whether the selector is enabled for interaction
 */
@Composable
fun EmojiRatingSelector(
    selectedRating: RatingValue?,
    onRatingSelected: (RatingValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Emoji row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RatingValue.entries.forEach { rating ->
                EmojiButton(
                    rating = rating,
                    isSelected = selectedRating == rating,
                    onClick = { if (enabled) onRatingSelected(rating) },
                    enabled = enabled
                )
            }
        }
        
        // Selected rating label
        if (selectedRating != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = selectedRating.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Individual emoji button for rating selection.
 */
@Composable
private fun EmojiButton(
    rating: RatingValue,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        label = "scale"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            rating.toColor().copy(alpha = 0.2f)
        } else {
            Color.Transparent
        },
        label = "backgroundColor"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) {
            rating.toColor()
        } else {
            Color.Transparent
        },
        label = "borderColor"
    )
    
    Box(
        modifier = modifier
            .size(56.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = borderColor,
                shape = CircleShape
            )
            .clickable(enabled = enabled, onClick = onClick)
            .semantics {
                contentDescription = "${rating.emoji} - ${rating.label}"
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rating.emoji,
            style = EmojiStyle,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Large emoji display for showing selected rating.
 * 
 * @param rating The rating to display
 * @param modifier Modifier for the component
 */
@Composable
fun EmojiRatingDisplay(
    rating: RatingValue,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = rating.emoji,
            style = EmojiLargeStyle
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = rating.label,
            style = MaterialTheme.typography.bodySmall,
            color = rating.toColor()
        )
    }
}

/**
 * Compact emoji rating indicator for lists and history views.
 * 
 * @param rating The rating to display, or null for unrated
 * @param modifier Modifier for the component
 */
@Composable
fun EmojiRatingIndicator(
    rating: RatingValue?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
                if (rating != null) {
                    rating.toColor().copy(alpha = 0.1f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rating?.emoji ?: "−",
            style = MaterialTheme.typography.titleMedium,
            color = if (rating != null) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

/**
 * Maps a RatingValue to its associated color.
 */
private fun RatingValue.toColor(): Color = when (this) {
    RatingValue.POSITIVE -> RatingColors.Positive
    RatingValue.NEUTRAL -> RatingColors.Neutral
    RatingValue.NEGATIVE -> RatingColors.Negative
}
