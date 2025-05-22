import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.woutwerkman.App

fun main(args: Array<String>) = application {
    val width = args.getOrNull(0)?.toFloatOrNull() ?: 383f
    val height = args.getOrNull(1)?.toFloatOrNull() ?: 740f
    val x = args.getOrNull(2)?.toFloatOrNull() ?: 379f
    val y = args.getOrNull(3)?.toFloatOrNull() ?: 176f

    val windowState = rememberWindowState(
        width = width.dp,
        height = height.dp,
        position = WindowPosition(x.dp, y.dp),
    )

    Window(
        title = "KotlinConfChallenge25",
        state = windowState,
        onCloseRequest = ::exitApplication,
        alwaysOnTop = true,
    ) { App() }
}