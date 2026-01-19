package com.dayrater.ui.insights.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dayrater.data.local.entity.RatingValue
import com.dayrater.domain.model.DayIndicator
import com.dayrater.domain.model.MonthlyCalendarData
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * Calendar heat map showing color-coded days based on ratings.
 */
@Composable
fun CalendarHeatMap(
    calendarData: MonthlyCalendarData,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Day of week headers
        WeekdayHeaders(firstDayOfWeek = calendarData.firstDayOfWeek)
        
        // Calendar grid
        CalendarGrid(
            calendarData = calendarData,
            onDayClick = onDayClick
        )
    }
}

@Composable
private fun WeekdayHeaders(firstDayOfWeek: DayOfWeek) {
    val days = generateDaySequence(firstDayOfWeek)
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        days.forEach { day ->
            Text(
                text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    calendarData: MonthlyCalendarData,
    onDayClick: (LocalDate) -> Unit
) {
    val totalCells = calendarData.startOffset + calendarData.daysInMonth
    val rows = (totalCells + 6) / 7 // Ceiling division
    
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNumber = cellIndex - calendarData.startOffset + 1
                    
                    if (dayNumber in 1..calendarData.daysInMonth) {
                        val dayIndicator = calendarData.dayRatings[dayNumber]
                        DayCell(
                            dayNumber = dayNumber,
                            indicator = dayIndicator,
                            onClick = { 
                                dayIndicator?.let { onDayClick(it.date) }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // Empty cell
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    dayNumber: Int,
    indicator: DayIndicator?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (indicator?.overallRating) {
        RatingValue.POSITIVE -> Color(0xFF4CAF50) // Green
        RatingValue.NEUTRAL -> Color(0xFFFFEB3B)  // Yellow
        RatingValue.NEGATIVE -> Color(0xFFF44336) // Red
        null -> Color.Transparent
    }
    
    val textColor = when (indicator?.overallRating) {
        RatingValue.POSITIVE, RatingValue.NEGATIVE -> Color.White
        RatingValue.NEUTRAL -> Color.Black
        null -> MaterialTheme.colorScheme.onSurface
    }
    
    val isToday = indicator?.date == LocalDate.now()
    
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(
                if (isToday) {
                    Modifier.background(
                        MaterialTheme.colorScheme.primaryContainer,
                        CircleShape
                    )
                } else Modifier
            )
            .clickable(enabled = indicator != null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayNumber.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = if (isToday && indicator?.overallRating == null) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                textColor
            }
        )
    }
}

private fun generateDaySequence(firstDayOfWeek: DayOfWeek): List<DayOfWeek> {
    val days = DayOfWeek.entries
    val startIndex = days.indexOf(firstDayOfWeek)
    return days.drop(startIndex) + days.take(startIndex)
}
