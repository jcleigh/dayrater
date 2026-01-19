package com.dayrater.ui.insights.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dayrater.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Navigation component for browsing weeks.
 */
@Composable
fun WeekNavigator(
    weekStart: LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    canGoNext: Boolean = true,
    modifier: Modifier = Modifier
) {
    val weekEnd = weekStart.plusDays(6)
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    val title = "${weekStart.format(formatter)} - ${weekEnd.format(formatter)}"
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousWeek) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.weekly_prev)
            )
        }
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        
        IconButton(
            onClick = onNextWeek,
            enabled = canGoNext
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.weekly_next)
            )
        }
    }
}
