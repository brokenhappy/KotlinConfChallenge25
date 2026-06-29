package com.woutwerkman

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.awaitApplication
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.roundToInt

data class TreeNode(val label: String, val children: List<TreeNode> = emptyList())

val tree = TreeNode("Types", listOf(
    TreeNode("Java", listOf(
        TreeNode("Implicit types"),
        TreeNode("Method Chains"),
    )),
    TreeNode("Kotlin", listOf(
        TreeNode("Property types"),
        TreeNode("Local variable types"),
        TreeNode("Function return types"),
    )),
))

suspend fun main() {
    awaitApplication {
        Window(onCloseRequest = { exitApplication() }, title = "UI fiddles", alwaysOnTop = true) {
            Column {
                Spacer(modifier = Modifier.height(30.dp))
                var rootValue by remember { mutableStateOf<Int?>(0) }
                RenderTree(tree, rootValue, onValueChange = { rootValue = it })
            }
        }
    }
}

@Composable
fun RenderTree(
    tree: TreeNode,
    value: Int?,
    onValueChange: (Int?) -> Unit,
) {
    Column {
        val childStates = remember(tree) { tree.children.map { 0 }.toMutableStateList<Int?>() }
        LaunchedEffect(value) {
            value?.let { newValue ->
                repeat(childStates.size) { childStates[it] = newValue }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedQuadStateCheckbox(
                value = value,
                onValueChange = { newValue ->
                    onValueChange(newValue)
                },
            )
            Text(tree.label)
        }

        Row {
            Spacer(modifier = Modifier.width(50.dp))
            Column {
                tree.children.forEachIndexed { index, node ->
                    RenderTree(
                        node,
                        childStates[index],
                        onValueChange = { newValue ->
                            childStates[index] = newValue
                            val childOptions = childStates.toSet()
                            if (null in childOptions || childOptions.size > 1) onValueChange(null)
                            else if (childOptions.size == 1) onValueChange(newValue)
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimatedQuadStateCheckbox(
    /** Null means it's undecided. Applicable to a parent checkbox whose children have multiple different states */
    value: Int?,
    onValueChange: (Int) -> Unit,
) {
    var targetValue = value?.div(3f)
    var overriddenByDragValue by remember { mutableStateOf<Float?>(null) }

    val anim = remember { Animatable(0f) }

    LaunchedEffect(targetValue) {
        targetValue?.let { anim.animateTo(it, animationSpec = tween(300)) }
    }

    val value = overriddenByDragValue ?: anim.value.takeUnless { targetValue == null }

    fun onClick() {
        val newTarget = if (targetValue in setOf(1f, null)) 0f else 1f
        targetValue = newTarget
        onValueChange((newTarget * 3).roundToInt())
    }

    val state = when (value) {
        null -> ToggleableState.Indeterminate
        1f -> ToggleableState.On
        0f -> ToggleableState.Off
        else -> ToggleableState.Indeterminate
    }


    val scope = rememberCoroutineScope()


    Box {
        if (overriddenByDragValue == null && targetValue == value || value == null) {
            TriStateCheckbox(
                state = state,
                onClick = { onClick() }
            )
        } else {
            Row(modifier = Modifier.width(48.dp)) {
                Spacer(Modifier.width(16.dp))
                Column {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .size(18.dp),
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        repeat(4) { i ->
                            if (i != 0) {
                                Box(Modifier.weight(5f))
                            }
                            val y = bell(value.toDouble(), width = 1 / 3.0, center = i / 3.0).toFloat()
                            Box(
                                Modifier
                                    .weight(2f + y)
                                    .height((5 + 7 * y).dp)
                                    .background(Color.Black)
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
                Spacer(Modifier.height(16.dp))
            }
            if (overriddenByDragValue != null) {
                Popup(
                    alignment = Alignment.TopStart,
                    offset = IntOffset(0, -50),
                    onDismissRequest = { },
                    properties = PopupProperties(),
                ) {
                    Surface(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        val currentIndicatedValue = (value * 3f).roundToInt()
                        Text(
                            when (currentIndicatedValue) {
                                0 -> "Always off"
                                1 -> "Only visible in extended X-ray mode"
                                2 -> "Only visible in X-ray mode"
                                else -> "Always on"
                            },
                            modifier = Modifier.padding(8.dp),
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        onClick()
                    }
                }
                .pointerInput(Unit) {
                    val offsetPx = 16.dp.toPx()
                    val checkboxWidthPx = 18.dp.toPx()
                    detectDragGestures(
                        onDragStart = { offset ->
                            overriddenByDragValue = ((offset.x - offsetPx) / checkboxWidthPx).coerceIn(0f, 1f)
                        },
                        onDrag = { change, _ ->
                            overriddenByDragValue = ((change.position.x - offsetPx) / checkboxWidthPx).coerceIn(0f, 1f)
                        },
                        onDragEnd = {
                            overriddenByDragValue?.let { newlyDecidedValue ->
                                val newlyDecidedInt = (newlyDecidedValue * 3).roundToInt()
                                onValueChange(newlyDecidedInt)
                                scope.launch {
                                    anim.snapTo(newlyDecidedValue)
                                    overriddenByDragValue = null
                                    anim.animateTo(newlyDecidedInt / 3f)
                                }
                            }
                        }
                    )
                }
        )
    }
}

fun bell(x: Double, center: Double, width: Double): Double {
    val t = (x - center) / width
    return exp(-4.0 * ln(2.0) * t * t)
}