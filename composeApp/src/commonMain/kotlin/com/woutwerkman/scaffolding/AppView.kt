package com.woutwerkman.scaffolding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val teamBlue = false
    val color1 = when (teamBlue) {
        true -> Color(0xff182d8c)
        else -> Color(0xff9c0c0e)
    }
    val color2 = when (teamBlue) {
        true -> Color(0xff3950ce)
        else -> Color(0xffbd2b1e)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier.weight(0.2f)
                .height(100.dp)
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Team indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.33f)
                    .border(10.dp, color1, RoundedCornerShape(10))
                    .background(Color.White, RoundedCornerShape(10))
                    .padding(20.dp)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val teamText = when (teamBlue) {
                    true -> "Team Blue"
                    else -> "Team Red"
                }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = teamText,
                    textAlign = TextAlign.Center,
                    fontSize = 110.sp,
                    color = Color.Black,
                )
            }

            Spacer(modifier = Modifier.width(10.dp))
            val timeUntilEndOfChallenge by countdownTo(currentChallenge.endTime, interval = 10.milliseconds)
                .shareAsState(Duration.ZERO)

            // Countdown
            Row(
                Modifier
                    // Whatever else you need
                    .background(
                        MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(10)
                    )
                    .padding(20.dp)
                    .fillMaxWidth(0.5f),
                horizontalArrangement = Arrangement.Center,
            ) {
                val timeShownOnTimer =
                    if (timeUntilEndOfChallenge > currentChallenge.duration)
                        timeUntilEndOfChallenge - currentChallenge.duration
                    else
                        timeUntilEndOfChallenge
                CountDownDisplay(timeShownOnTimer, currentChallenge.duration)
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (timeUntilEndOfChallenge <= currentChallenge.duration) {
                ChallengeStatusIndicator("Challenge running", color1, 60.sp)
            } else {
                ChallengeStatusIndicator("Preparing for next Challenge ⏸️", color1, 50.sp)
            }
        }
        Row(
            modifier = Modifier.weight(0.7f)
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(16 / 9f)
            ) {
                WebsiteView(
                    modifier = Modifier
                        .aspectRatio(16 / 9f)
                        .weight(16 / 9f),
                    teamName = if (teamBlue) "Blue" else "Red",
                    {
                        Content()
                    },
                    color2
                )
            }
            Column(
                modifier = Modifier
                    .weight(94 / 197f),
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
}

@Composable
fun Content() {
    Box(modifier = Modifier.fillMaxSize()) {
        App()
    }
}

@Composable
private fun ChallengeStatusIndicator(text: String, borderColor: Color, fontSize: TextUnit) {
    Row(
        modifier = Modifier.fillMaxWidth(1f)
            .border(
                10.dp,
                borderColor,
                RoundedCornerShape(10)
            )
            .background(Color.White, RoundedCornerShape(10))
            .padding(20.dp)
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            textAlign = TextAlign.Center,
            fontSize = fontSize
        )
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
    Row(modifier = Modifier.aspectRatio(15 / 4f)) {
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
