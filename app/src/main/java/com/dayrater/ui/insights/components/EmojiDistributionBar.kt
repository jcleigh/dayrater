package com.dayrater.ui.insights.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dayrater.data.local.entity.RatingValue
import com.dayrater.domain.model.CategorySummary

/**
 * Displays emoji distribution as text counts (e.g., "3😊 2😐 1😢").
 */
@Composable
fun EmojiDistributionBar(
    summary: CategorySummary,
    modifier: Modifier = Modifier
) {
    if (summary.totalRatings == 0) {
        Text(
            text = "—",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier
        )
        return
    }
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (summary.positiveCount > 0) {
            EmojiCount(RatingValue.POSITIVE.emoji, summary.positiveCount)
        }
        if (summary.neutralCount > 0) {
            EmojiCount(RatingValue.NEUTRAL.emoji, summary.neutralCount)
        }
        if (summary.negativeCount > 0) {
            EmojiCount(RatingValue.NEGATIVE.emoji, summary.negativeCount)
        }
    }
}

@Composable
private fun EmojiCount(emoji: String, count: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = emoji,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Visual bar showing rating distribution proportionally.
 */
@Composable
fun EmojiDistributionVisualBar(
    summary: CategorySummary,
    modifier: Modifier = Modifier
) {
    if (summary.totalRatings == 0) {
        Box(
            modifier = modifier
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        return
    }
    
    val total = summary.totalRatings.toFloat()
    
    Row(
        modifier = modifier
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
    ) {
        if (summary.positiveCount > 0) {
            Box(
                modifier = Modifier
                    .weight(summary.positiveCount / total)
                    .height(8.dp)
                    .background(Color(0xFF4CAF50)) // Green
            )
        }
        if (summary.neutralCount > 0) {
            Box(
                modifier = Modifier
                    .weight(summary.neutralCount / total)
                    .height(8.dp)
                    .background(Color(0xFFFFEB3B)) // Yellow
            )
        }
        if (summary.negativeCount > 0) {
            Box(
                modifier = Modifier
                    .weight(summary.negativeCount / total)
                    .height(8.dp)
                    .background(Color(0xFFF44336)) // Red
            )
        }
    }
}
