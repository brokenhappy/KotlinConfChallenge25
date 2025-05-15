import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.woutwerkman.scaffolding.AppPreview

fun main() = application {
    Window(
        title = "KotlinConfChallenge25",
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        onCloseRequest = ::exitApplication,
    ) {
        AppPreview()
    }
}

