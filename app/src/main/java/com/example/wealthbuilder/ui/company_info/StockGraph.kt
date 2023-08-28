package com.example.wealthbuilder.ui.company_info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.wealthbuilder.domain.model.IntradayInfo
import kotlin.math.roundToInt
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import kotlin.math.round

@Composable
fun StockGraph(
    stockTrades: List<IntradayInfo> = emptyList(),
    modifier: Modifier,
    graphColor: Color = Color.Green
) {
    val spacing = 100f
    val transparentGraphColor = remember {
        graphColor.copy(alpha = 0.5f)
    }
    val upperBound = remember {
        stockTrades.maxOfOrNull { it.close }?.plus(1)?.roundToInt() ?: 0
    }
    val lowerBound = remember {
        stockTrades.minOfOrNull { it.close }?.toInt() ?: 0
    }

    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
        }
    }
    Canvas(modifier = Modifier) {
        val spacePerHour = (size.width - spacing) / stockTrades.size
        (0 until stockTrades.size - 1 step 2).forEach { i ->
            val stockTrade = stockTrades[i]
            val tradingHour = stockTrade.timeStamp.hour
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    tradingHour.toString(),
                    spacing + i * spacePerHour,
                    size.height - spacing,
                    textPaint
                )
            }
        }

        val priceStep = (upperBound - lowerBound) / 5
        (0..5).forEach { i ->
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    round((priceStep * i + lowerBound).toDouble()).toString(),
                    spacing,
                    size.height - spacing - i * size.height / 5,
                    textPaint
                )
            }
        }

        var lastX = 0f

    }

}