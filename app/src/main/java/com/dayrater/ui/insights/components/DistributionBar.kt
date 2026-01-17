package com.dayrater.ui.insights.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dayrater.domain.model.RatingDistribution

/**
 * Horizontal bar showing rating distribution with percentages.
 */
@Composable
fun DistributionBar(
    distribution: RatingDistribution,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Visual bar
        if (distribution.total > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                if (distribution.positiveCount > 0) {
                    Box(
                        modifier = Modifier
                            .weight(distribution.positiveCount.toFloat())
                            .height(24.dp)
                            .background(Color(0xFF4CAF50))
                    )
                }
                if (distribution.neutralCount > 0) {
                    Box(
                        modifier = Modifier
                            .weight(distribution.neutralCount.toFloat())
                            .height(24.dp)
                            .background(Color(0xFFFFEB3B))
                    )
                }
                if (distribution.negativeCount > 0) {
                    Box(
                        modifier = Modifier
                            .weight(distribution.negativeCount.toFloat())
                            .height(24.dp)
                            .background(Color(0xFFF44336))
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Legend with counts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DistributionLegendItem(
                emoji = "😊",
                count = distribution.positiveCount,
                percent = distribution.positivePercent,
                color = Color(0xFF4CAF50)
            )
            DistributionLegendItem(
                emoji = "😐",
                count = distribution.neutralCount,
                percent = distribution.neutralPercent,
                color = Color(0xFFFFEB3B)
            )
            DistributionLegendItem(
                emoji = "😢",
                count = distribution.negativeCount,
                percent = distribution.negativePercent,
                color = Color(0xFFF44336)
            )
        }
    }
}

@Composable
private fun DistributionLegendItem(
    emoji: String,
    count: Int,
    percent: Float,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(12.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Text(
            text = emoji,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "$count (${percent.toInt()}%)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
