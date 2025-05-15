package com.woutwerkman.scaffolding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun SevenSegmentDigit(
    digit: Int,
    modifier: Modifier,
    activeColor: Color,
    inactiveColor: Color,
    segmentWidth: Dp,
    segmentsSpace: Dp,
) {
    SingleSevenSegment(
        activeSegments = when (digit) {
            0 -> "1110111"
            1 -> "0010010"
            2 -> "1011101"
            3 -> "1011011"
            4 -> "0111010"
            5 -> "1101011"
            6 -> "1101111"
            7 -> "1010010"
            8 -> "1111111"
            9 -> "1111011"
            else -> error("Invalid digit: $digit")
        }.map { it == '1' },
        modifier,
        activeColor,
        inactiveColor,
        segmentWidth,
        segmentsSpace,
    )
}