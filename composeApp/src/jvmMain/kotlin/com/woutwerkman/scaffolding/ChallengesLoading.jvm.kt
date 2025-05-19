package com.woutwerkman.scaffolding

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.nio.file.*
import kotlin.io.path.readText

fun fileContentOf(path: String): Flow<String> = channelFlow {
    val path = Paths.get(path)
    send(path.readText())

    val directory = path.parent
    val pathRelativeToDirectory = path.fileName

    FileSystems.getDefault().newWatchService().use { watchService ->
        directory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)

        withContext(Dispatchers.IO) {
            while (true) {
                yield()
                val key = runInterruptible { watchService.take() }
                try {
                    for (event in key.pollEvents()) {
                        event
                            ?.takeUnless { it.kind() == StandardWatchEventKinds.OVERFLOW }
                            ?.context()
                            ?.let { it as? Path }
                            ?.takeIf { fileEventPathRelativeToDir ->
                                fileEventPathRelativeToDir == pathRelativeToDirectory
                            }
                            ?.also { send(path.readText()) }
                    }
                } finally {
                    key.reset()
                }
            }
        }
    }
}
