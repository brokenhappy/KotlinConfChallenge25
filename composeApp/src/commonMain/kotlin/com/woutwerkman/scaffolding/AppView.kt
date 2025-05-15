@file:OptIn(ExperimentalTime::class)

package com.woutwerkman.scaffolding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.woutwerkman.App
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.times


@Composable
@Preview
fun AppPreview() {
    Column {
        Column(Modifier.weight(1f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFCCCCCC)),
            ) {
                WebsiteView(
                    modifier = Modifier
                        .aspectRatio(16 / 9f)
                        .weight(16 / 9f)
                ) {
                    Content()
                }
                PhoneView(
                    modifier = Modifier
                        .aspectRatio(94 / 197f)
                        .weight(94 / 197f),
                ) {
                    Content()
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFCCCCCC)),
            )
        }
        Row(Modifier
            .size(width = 750.dp, height = 200.dp)
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(10.dp)
        ) {
            CountDownDisplay(countdownTo(Clock.System.now() + 7.minutes + 5.seconds, interval = 10.milliseconds))
        }
    }
}

@Composable
fun Content() {
    Box(modifier = Modifier.fillMaxSize()) {
        App()
    }
}


private fun countdownTo(instant: Instant, interval: Duration): Flow<Duration> = flow {
    emit(instant - Clock.System.now())
    while (true) {
        val now = Clock.System.now()
        val distance = instant - now
        if (distance <= 0.nanoseconds) {
            emit(Duration.ZERO)
            break
        }
        delay(((distance / interval) % 1.0) * interval)
        emit(distance)
    }
}

@Composable
private fun CountDownDisplay(countDown: Flow<Duration>) {
    val timeLeft by countDown.collectAsState(Duration.ZERO)
    TimeDisplay(
        timeLeft,
        when {
            timeLeft < 3.minutes -> Color(0xFFE80000)
            timeLeft < 7.minutes -> Color(0xFFF19900)
            else -> Color(0xFF00FF00)
        },
        MaterialTheme.colorScheme.onBackground,
        20.dp,
        2.dp,
    )
}

@Composable
private fun TimeDisplay(
    time: Duration,
    activeColor: Color,
    inactiveColor: Color,
    segmentWidth: Dp,
    segmentSpace: Dp,
) {
    time.toComponents { hours, minutes, seconds, nanoseconds ->
        TwoSevenSegmentDigits(minutes, activeColor, inactiveColor, segmentWidth, segmentSpace)
        Spacer(Modifier.width(segmentWidth))
        Colon(
            modifier = Modifier.fillMaxHeight(),
            activeColor = activeColor,
            segmentWidth = segmentWidth * 1.3,
        )
        Spacer(Modifier.width(20.dp))
        TwoSevenSegmentDigits(seconds, activeColor, inactiveColor, segmentWidth, segmentSpace)
        Spacer(Modifier.width(segmentWidth))
        Column {
            Spacer(Modifier.weight(1f))
            Row(Modifier.weight(1f)) {
                Colon(
                    modifier = Modifier.fillMaxHeight(),
                    activeColor = activeColor,
                    segmentWidth = segmentWidth,
                )
                Spacer(Modifier.width(10.dp))
                TwoSevenSegmentDigits(nanoseconds / 10_000_000, activeColor, inactiveColor, segmentWidth / 2, segmentSpace / 2)
            }
        }
    }
}

@Composable
private fun TwoSevenSegmentDigits(
    minutes: Int,
    activeColor: Color,
    inactiveColor: Color,
    segmentWidth: Dp,
    segmentSpace: Dp,
) {
    SevenSegmentDigit(
        digit = (minutes / 10) % 10,
        modifier = Modifier.fillMaxHeight(),
        activeColor = activeColor,
        inactiveColor = inactiveColor,
        segmentWidth = segmentWidth,
        segmentsSpace = segmentSpace,
    )
    Spacer(Modifier.width(30.dp))
    SevenSegmentDigit(
        digit = minutes % 10,
        modifier = Modifier.fillMaxHeight(),
        activeColor = activeColor,
        inactiveColor = inactiveColor,
        segmentWidth = segmentWidth,
        segmentsSpace = segmentSpace,
    )
}

private operator fun Dp.times(rhs: Double): Dp = (value * rhs).dp
