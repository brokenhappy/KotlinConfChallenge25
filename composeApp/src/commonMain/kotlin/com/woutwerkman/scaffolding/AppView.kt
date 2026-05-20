package com.woutwerkman.scaffolding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.woutwerkman.App
import kotlinconfchallenge25.composeapp.generated.resources.Res
import kotlinconfchallenge25.composeapp.generated.resources.challenge_image_cool_kodee
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.times

@Serializable
data class Challenge(val endTime: Instant, val duration: Duration, val imageUrl: String)

private const val teamBlue = false

enum class ChallengePhase { PREPARING, RUNNING, SUSPENSE, VOTING }

@Composable
fun AppPreview(currentChallenge: Challenge, voteStatus: VoteStatus? = null) {
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
        val timeUntilEndOfChallenge by countdownTo(currentChallenge.endTime, interval = 10.milliseconds)
            .shareAsState(Duration.ZERO, key = currentChallenge.endTime)

        val phase = when {
            timeUntilEndOfChallenge > currentChallenge.duration -> ChallengePhase.PREPARING
            timeUntilEndOfChallenge > 2.minutes -> ChallengePhase.RUNNING
            timeUntilEndOfChallenge > Duration.ZERO -> ChallengePhase.SUSPENSE
            else -> ChallengePhase.VOTING
        }

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

            when (phase) {
                ChallengePhase.PREPARING -> ChallengeStatusIndicator("Preparing for next Challenge ⏸️", color1, 50.sp)
                ChallengePhase.RUNNING -> ChallengeStatusIndicator("Challenge running", color1, 60.sp)
                ChallengePhase.SUSPENSE -> ChallengeStatusIndicator("Time almost up! Get ready to vote!", color1, 40.sp)
                ChallengePhase.VOTING -> ChallengeStatusIndicator("Voting time!", color1, 60.sp)
            }
        }

        val showOverlay = phase == ChallengePhase.SUSPENSE || phase == ChallengePhase.VOTING
        val blurRadius = if (showOverlay) 48.dp else 0.dp

        @Composable
        fun PreviewContent() {
            when (phase) {
                ChallengePhase.PREPARING -> Ad()
                else -> Content()
            }
        }

        Box(modifier = Modifier.weight(0.7f)) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (showOverlay) Modifier.blur(blurRadius) else Modifier)
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
                        color2,
                    ) {
                        PreviewContent()
                    }
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
                        PreviewContent()
                    }
                }
            }

            if (phase == ChallengePhase.SUSPENSE) {
                SuspenseOverlay(color2, color1)
            }
            if (phase == ChallengePhase.VOTING) {
                VoteResultsOverlay(voteStatus, teamBlue)
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
fun Ad() {
    BoxWithConstraints(
        modifier = Modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        if (maxWidth > maxHeight) {
            Row {
                InternalAd(isWide = true)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 10.dp,
                            bottomEnd = 10.dp,
                        )
                    )
                ,
            ) {
                InternalAd(isWide = false)
            }
        }
    }
}

@Composable
fun InternalAd(isWide: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(painterResource(Res.drawable.challenge_image_cool_kodee), null)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column {
                Text(
                    """
                    ⚔️ Welcome to the UI Arena! 🎨
                """.trimIndent(),
                    color = Color.White,
                    fontSize = if (isWide) 30.sp else 18.sp,
                    fontWeight = Bold,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = """
                    Two teams. One challenge.
                    Build it with Compose Multiplatform.
                    No previews. No mercy.
                    Ten minutes to glory.
                """.trimIndent(),
                    fontSize = if (isWide) 30.sp else 18.sp,
                    color = Color.White,
                )

            }
        }
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
    key: Any?,
    scope: CoroutineScope = rememberCoroutineScope(),
    started: SharingStarted = SharingStarted.Eagerly,
    replay: Int = 1
): State<T> = remember(key) { shareIn(scope, started, replay) }.collectAsState(initial)


private operator fun Dp.times(rhs: Double): Dp = (value * rhs).dp
