import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.woutwerkman.scaffolding.AppPreview
import com.woutwerkman.scaffolding.Challenge
import com.woutwerkman.scaffolding.fileContentOf
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import org.jetbrains.compose.reload.agent.orchestration
import org.jetbrains.compose.reload.orchestration.OrchestrationMessage.ReloadClassesResult
import org.jetbrains.compose.reload.orchestration.invokeWhenReceived
import java.util.Base64
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

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
    ) {
        orchestration.invokeWhenReceived<ReloadClassesResult> { result ->
            if (!result.isSuccess) {
                startSameProgramWithWindowState(windowState)
                exitProcess(0)
            }
        }
        val challenges: List<Challenge>? by produceState(null) {
            val path = System.getenv("KotlinConfChallengeDataFile")
                ?: "${System.getProperty("user.home")}/Documents/filesForKotlinConfChallenge25/downloadCache.json".also {
                    println("No env set for 'KotlinConfChallengeDataFile', defaulting to: $it")
                }

            fileContentOf(path).collect { fileContent ->
                val cache = try {
                    Json.decodeFromString<DownloadCache>(fileContent)
                } catch (e: Exception) {
                    println("Error parsing cache file: $e")
                    return@collect
                }
                value = cache.challengesCache
            }
        }
        if (challenges == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Loading challenges...", fontSize = 20.sp)
            }
            return@Window
        }

        val now by produceState(Clock.System.now()) {
            while (true) {
                delay(100.milliseconds)
                value = Clock.System.now()
            }
        }

        val currentChallenge = remember(now, challenges) { challenges?.firstOrNull { now < (it.endTime + 3.minutes) } }
        if (currentChallenge == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Last challenge has been played...", fontSize = 20.sp)
            }
            return@Window
        }


        AppPreview(currentChallenge)
    }
}

private const val uniqueString = "UNIQUEIDENTIFIER2137164164781298172481724"

private fun startSameProgramWithWindowState(windowState: WindowState) {
    val currentProcess = ProcessHandle.current().info()
    ProcessBuilder(
        currentProcess.command().orElseThrow(),
        *currentProcess.arguments().get().takeWhile { it != uniqueString }.toTypedArray(),
        uniqueString,
        windowState.size.width.value.toString(),
        windowState.size.height.value.toString(),
        windowState.position.x.value.toString(),
        windowState.position.y.value.toString(),
    ).start()
}

@Serializable
private class DownloadCache(
    val imageCache: Map<String, Blob>,
    val challengesCache: List<Challenge>?,
    val lastPoll: Instant,
)

@Serializable(with = Blob.BlobSerializer::class)
class Blob(private val data: ByteArray) {
    fun toByteArray(): ByteArray = data.clone()
    override fun equals(other: Any?): Boolean = this === other || other is Blob && data.contentEquals(other.data)
    override fun hashCode(): Int = data.contentHashCode()

    companion object {
        fun fromByteArray(bytes: ByteArray): Blob = Blob(bytes.clone())
        fun fromBase64(base64: String): Blob = Blob(Base64.getDecoder().decode(base64))
    }

    object BlobSerializer : KSerializer<Blob> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Blob", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Blob) {
            encoder.encodeString(Base64.getEncoder().encodeToString(value.data))
        }

        override fun deserialize(decoder: Decoder): Blob = fromBase64(decoder.decodeString())
    }
}
