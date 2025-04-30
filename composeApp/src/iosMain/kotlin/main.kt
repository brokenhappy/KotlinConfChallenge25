import androidx.compose.ui.window.ComposeUIViewController
import com.woutwerkman.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
