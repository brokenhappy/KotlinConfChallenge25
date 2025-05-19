import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.woutwerkman.App

fun main(args: Array<String>) = application {
    val width = args.getOrNull(0)?.toFloatOrNull() ?: 800f
    val height = args.getOrNull(1)?.toFloatOrNull() ?: 600f
    val x = args.getOrNull(2)?.toFloatOrNull()
    val y = args.getOrNull(3)?.toFloatOrNull()

    val windowState = rememberWindowState(
        width = width.dp,
        height = height.dp,
        position = if (x != null && y != null) {
            WindowPosition(x.dp, y.dp)
        } else WindowPosition.PlatformDefault,
    )

    Window(
        title = "KotlinConfChallenge25",
        state = windowState,
        onCloseRequest = ::exitApplication,
        alwaysOnTop = true,
    ) { App() }
}