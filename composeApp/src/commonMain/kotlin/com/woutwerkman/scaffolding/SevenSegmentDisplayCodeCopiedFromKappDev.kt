package com.woutwerkman.scaffolding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp

/**
 * All credits here goes to KappDev: https://medium.com/@kappdev/creating-a-seven-segment-view-in-jetpack-compose-e217209780fe
 */
@Composable
fun SingleSevenSegment(
    activeSegments: List<Boolean>,
    modifier: Modifier,
    activeColor: Color,
    inactiveColor: Color,
    segmentWidth: Dp,
    segmentsSpace: Dp
) {
    Canvas(
        // This modifier ensures that the view is displayed in a correct ratio of 2:1
        modifier = modifier.aspectRatio(0.5f, matchHeightConstraintsFirst = true)
    ) {
        val halfViewHeight = (size.height / 2)
        val halfWidth = (segmentWidth.toPx() / 2)

        val rightEdge = (size.width - halfWidth)
        val bottomEdge = (size.height - halfWidth)

        listOf(
            SegmentLocationData(isVertical = false, halfWidth, rightEdge, halfWidth, halfWidth),
            SegmentLocationData(isVertical = true, halfWidth, halfWidth, halfWidth, halfViewHeight),
            SegmentLocationData(isVertical = true, rightEdge, rightEdge, halfWidth, halfViewHeight),
            SegmentLocationData(isVertical = false, halfWidth, rightEdge, halfViewHeight, halfViewHeight),
            SegmentLocationData(isVertical = true, halfWidth, halfWidth, halfViewHeight, bottomEdge),
            SegmentLocationData(isVertical = true, rightEdge, rightEdge, halfViewHeight, bottomEdge),
            SegmentLocationData(isVertical = false, halfWidth, rightEdge, bottomEdge, bottomEdge),
        )
            .zip(activeSegments)
            .forEach { (data, isActive) ->
                drawSegment(
                    color = if (isActive) activeColor else inactiveColor,
                    data = data.addSpacing(segmentsSpace.toPx()),
                    halfWidth = halfWidth,
                )
            }
    }
}

@Composable
fun Colon(
    modifier: Modifier,
    activeColor: Color,
    segmentWidth: Dp
) {
    Canvas(
        // This modifier ensures that the view is displayed in a correct ratio
        modifier = modifier.aspectRatio(0.17f, matchHeightConstraintsFirst = true)
    ) {
        val halfWidth = (segmentWidth.toPx() / 2)
        val rightEdge = (size.width - halfWidth)
        drawSegment(
            color = activeColor,
            data = SegmentLocationData(
                isVertical = true,
                startX = rightEdge,
                endX = rightEdge,
                startY = size.height / 22 * 15,
                endY = size.height / 22 * 15 + halfWidth * 2,
            ),
            halfWidth = halfWidth,
        )
        drawSegment(
            color = activeColor,
            data = SegmentLocationData(
                isVertical = true,
                startX = rightEdge,
                endX = rightEdge,
                startY = size.height / 22 * 7,
                endY = size.height / 22 * 7 + halfWidth * 2,
            ),
            halfWidth = halfWidth,
        )
    }
}

fun SegmentLocationData.addSpacing(space: Float): SegmentLocationData = when {
    isVertical -> this.copy(
        startY = this.startY + space,
        endY = this.endY - space,
    )
    else -> this.copy(
        startX = this.startX + space,
        endX = this.endX - space,
    )
}

data class SegmentLocationData(
    val isVertical: Boolean,
    val startX: Float,
    val endX: Float,
    val startY: Float,
    val endY: Float
)

private fun DrawScope.drawSegment(
    color: Color,
    data: SegmentLocationData,
    halfWidth: Float,
) {
    val segmentPath = Path().apply {
        with(data) {
            moveTo(startX, startY)
            if (isVertical) {
                lineTo(startX + halfWidth, startY + halfWidth)
                lineTo(startX + halfWidth, endY - halfWidth)
                lineTo(endX, endY)
                lineTo(startX - halfWidth, endY - halfWidth)
                lineTo(startX - halfWidth, startY + halfWidth)
            } else {
                lineTo(startX + halfWidth, startY - halfWidth)
                lineTo(endX - halfWidth, startY - halfWidth)
                lineTo(endX, endY)
                lineTo(endX - halfWidth, startY + halfWidth)
                lineTo(startX + halfWidth, startY + halfWidth)
            }
            close()
        }
    }
    drawPath(segmentPath, color)
}