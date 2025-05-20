package com.woutwerkman.scaffolding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.woutwerkman.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.times

@Serializable
data class Challenge(val endTime: Instant, val duration: Duration, val imageUrl: String)

@Composable
@Preview
fun AppPreview(currentChallenge: Challenge) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCCCCCC)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(16 / 9f)
        ) {
            WebsiteView(
                modifier = Modifier
                    .aspectRatio(16 / 9f)
                    .weight(16 / 9f)
            ) {
                Content()
            }

            Row(Modifier
                // Whatever else you need
                .weight(1f)
                .height(100.dp)
                .background(MaterialTheme.colorScheme.onBackground)
                .padding(20.dp)
                // Whatever else you need
            ) {
                val timeUntilEndOfChallenge by countdownTo(currentChallenge.endTime, interval = 10.milliseconds)
                    .shareAsState(Duration.ZERO)
                val timeShownOnTimer =
                    if (timeUntilEndOfChallenge > currentChallenge.duration)
                        timeUntilEndOfChallenge - currentChallenge.duration
                    else
                        timeUntilEndOfChallenge
                CountDownDisplay(timeShownOnTimer, currentChallenge.duration)
            }
        }
        Column(
            modifier = Modifier
                .weight(94 / 197f)
        ) {
            PhoneView(
                modifier = Modifier
                    .weight(3f)
                    .aspectRatio(94 / 197f)
                    .fillMaxHeight(),
            ) {
                Content()
            }
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
private fun CountDownDisplay(timeLeft: Duration, duration: Duration) {
    TimeDisplay(
        timeLeft,
        when {
            timeLeft < duration / 3 -> Color(0xFFE80000)
            timeLeft < duration * 2 / 3 -> Color(0xFFF19900)
            else -> Color(0xFF00FF00)
        },
        MaterialTheme.colorScheme.onBackground,
        20.dp,
        3.dp,
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
        if (hours <= 0) {
            TimeDisplay(
                firstDigits = minutes,
                secondDigits = seconds,
                smallDigits = nanoseconds / 10.milliseconds.inWholeNanoseconds.toInt(),
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                segmentWidth = segmentWidth,
                segmentSpace = segmentSpace,
            )
        } else {
            TimeDisplay(
                firstDigits = hours.toInt(),
                secondDigits = minutes,
                smallDigits = seconds,
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                segmentWidth = segmentWidth,
                segmentSpace = segmentSpace,
            )
        }
    }
}

@Composable
private fun TimeDisplay(
    firstDigits: Int,
    secondDigits: Int,
    smallDigits: Int,
    activeColor: Color,
    inactiveColor: Color,
    segmentWidth: Dp,
    segmentSpace: Dp
) {
    TwoSevenSegmentDigits(firstDigits, activeColor, inactiveColor, segmentWidth, segmentSpace)
    Spacer(Modifier.width(segmentWidth))
    Colon(
        modifier = Modifier.fillMaxHeight(),
        activeColor = activeColor,
        segmentWidth = segmentWidth * 1.3,
    )
    Spacer(Modifier.width(10.dp))
    TwoSevenSegmentDigits(secondDigits, activeColor, inactiveColor, segmentWidth, segmentSpace)
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
            TwoSevenSegmentDigits(
                smallDigits,
                activeColor,
                inactiveColor,
                segmentWidth / 2,
                segmentSpace / 2
            )
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
    Spacer(Modifier.width(15.dp))
    SevenSegmentDigit(
        digit = minutes % 10,
        modifier = Modifier.fillMaxHeight(),
        activeColor = activeColor,
        inactiveColor = inactiveColor,
        segmentWidth = segmentWidth,
        segmentsSpace = segmentSpace,
    )
}

@Composable
fun <T> Flow<T>.shareAsState(
    initial: T,
    scope: CoroutineScope = rememberCoroutineScope(),
    started: SharingStarted = SharingStarted.Eagerly,
    replay: Int = 1
): androidx.compose.runtime.State<T> = remember { shareIn(scope, started, replay) }.collectAsState(initial)


private operator fun Dp.times(rhs: Double): Dp = (value * rhs).dp
