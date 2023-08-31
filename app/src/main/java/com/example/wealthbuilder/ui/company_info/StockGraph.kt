package com.example.wealthbuilder.ui.company_info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.wealthbuilder.domain.model.IntradayInfo
import kotlin.math.roundToInt
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
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

        val priceStep = (upperBound - lowerBound) / 5f
        (0..4).forEach { i ->
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    round((priceStep * i + lowerBound).toDouble()).toString(),
                    30f,
                    size.height - spacing - i * size.height / 5,
                    textPaint
                )
            }
        }

        var lastX = 0f
        val strokePath = Path().apply {
            val height = size.height
            for (i in stockTrades.indices) {
                val trade = stockTrades[i]
                val nextTrade = stockTrades.getOrNull(i+1) ?: stockTrades.last()

                val ratioL = (trade.close - lowerBound) / (upperBound - lowerBound)
                val ratioR = (nextTrade.close - lowerBound) / (upperBound - lowerBound)

                val xL = spacing + i * spacePerHour
                val yL = (height - spacing) - ratioL.toFloat() * (height - spacing)
                val xR = spacing + (i + 1) * spacePerHour
                val yR = (height - spacing) - ratioR.toFloat() * (height - spacing)
                if (i == 0) {
                    moveTo(xL, yL)
                }
                quadraticBezierTo(xL, yL, (xL + xR) / 2f, (yL + yR) / 2f)
                lastX = (xL + xR) / 2f
            }
        }
        val fillPath = android.graphics.Path(strokePath.asAndroidPath())
            .asComposePath()
            .apply {
                lineTo(lastX, size.height-spacing)
                lineTo(spacing, size.height-spacing)
                close()
            }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    transparentGraphColor,
                    Color.Transparent
                ),
                endY = size.height - spacing
            ))
        drawPath(
            path = strokePath,
            color = graphColor,
            style = Stroke(
                width = 3.sp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}