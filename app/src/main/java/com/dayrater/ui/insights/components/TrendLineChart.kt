package com.dayrater.ui.insights.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dayrater.domain.model.TrendData
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer

/**
 * Line chart component for displaying rating trends using Vico library.
 */
@Composable
fun TrendLineChart(
    trendData: TrendData,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    
    // Convert data points to chart data
    val dataPoints = trendData.dataPoints.filter { it.value != null }
    
    if (dataPoints.isEmpty()) {
        return
    }
    
    // Update model with data
    remember(trendData) {
        modelProducer.runTransaction {
            lineSeries {
                series(dataPoints.map { it.value!! })
            }
        }
    }
    
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        remember {
                            LineCartesianLayer.LineFill.single(
                                com.patrykandpatrick.vico.core.common.Fill(
                                    android.graphics.Color.parseColor("#4CAF50")
                                )
                            )
                        }
                    )
                )
            ),
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis()
        ),
        modelProducer = modelProducer,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}
