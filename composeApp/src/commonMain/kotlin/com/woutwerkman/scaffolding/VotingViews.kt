package com.woutwerkman.scaffolding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SuspenseOverlay(teamColor: Color, teamDarkColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            PulsingText(teamColor)

            Spacer(Modifier.height(32.dp))

            QrCodePlaceholder()

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Scan to vote!",
                color = teamColor.copy(alpha = 0.8f),
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun PulsingText(accentColor: Color) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Exciting!",
            color = accentColor.copy(alpha = alpha),
            fontSize = (48 * scale).sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "What will the UI become?",
            color = Color.White.copy(alpha = alpha * 0.9f),
            fontSize = (32 * scale).sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Get ready to vote!",
            color = Color.White.copy(alpha = alpha * 0.8f),
            fontSize = (26 * scale).sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun QrCodePlaceholder() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellSize = size.width / 7
            val pattern = listOf(
                listOf(1,1,1,0,1,1,1),
                listOf(1,0,1,0,1,0,1),
                listOf(1,1,1,0,1,1,1),
                listOf(0,0,0,1,0,0,0),
                listOf(1,1,1,0,1,1,1),
                listOf(1,0,1,0,1,0,1),
                listOf(1,1,1,0,1,1,1),
            )
            for (row in pattern.indices) {
                for (col in pattern[row].indices) {
                    if (pattern[row][col] == 1) {
                        drawRoundRect(
                            color = Color.Black,
                            topLeft = Offset(col * cellSize, row * cellSize),
                            size = Size(cellSize - 2, cellSize - 2),
                            cornerRadius = CornerRadius(2f, 2f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VoteResultsOverlay(voteStatus: VoteStatus?, isBlueTeam: Boolean) {
    val teamColor = if (isBlueTeam) Color(0xFF42A5F5) else Color(0xFFEF5350)
    val teamName = if (isBlueTeam) "Blue" else "Red"
    val teamVotes = if (isBlueTeam) voteStatus?.number_of_blue_votes ?: 0
        else voteStatus?.number_of_red_votes ?: 0

    val bounceScale = remember { Animatable(1f) }
    LaunchedEffect(teamVotes) {
        if (teamVotes > 0) {
            bounceScale.snapTo(1f)
            bounceScale.animateTo(
                targetValue = 1.25f,
                animationSpec = tween(80, easing = FastOutSlowInEasing),
            )
            bounceScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = "Team $teamName",
                color = teamColor,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(16.dp))

            VoteCounterAnimated(teamVotes, teamColor, bounceScale.value)

            Spacer(Modifier.height(16.dp))

            val infiniteTransition = rememberInfiniteTransition()
            val dotAlpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800),
                    repeatMode = RepeatMode.Reverse,
                ),
            )

            if (voteStatus?.session_active == true) {
                Text(
                    text = "Voting is LIVE",
                    color = Color.Green.copy(alpha = dotAlpha),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
            } else {
                Text(
                    text = "Votes",
                    color = teamColor.copy(alpha = 0.7f),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun VoteCounterAnimated(count: Int, teamColor: Color, bounceScale: Float) {
    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    Box(
        modifier = Modifier
            .scale(bounceScale)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        teamColor.copy(alpha = glowAlpha * 0.3f),
                        Color.Transparent,
                    ),
                )
            )
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = count.toString(),
            color = teamColor,
            fontSize = 120.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
    }
}
