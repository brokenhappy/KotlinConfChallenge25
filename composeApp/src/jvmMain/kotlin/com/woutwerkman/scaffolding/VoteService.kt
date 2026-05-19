package com.woutwerkman.scaffolding

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.util.concurrent.CompletionStage
import kotlin.time.Duration.Companion.seconds

private val json = Json { ignoreUnknownKeys = true }

fun voteStatusFlow(serverUrl: String = "ws://169.254.145.215:8080"): Flow<VoteStatus> = callbackFlow {
    val wsUrl = "$serverUrl/ws/notSoHiddenUrlBecauseItWillBeInOpenSourceRepo/status"
    val client = HttpClient.newHttpClient()

    fun connect() {
        client.newWebSocketBuilder().buildAsync(URI.create(wsUrl), object : WebSocket.Listener {
            private val buffer = StringBuilder()

            override fun onText(webSocket: WebSocket, data: CharSequence, last: Boolean): CompletionStage<*>? {
                buffer.append(data)
                if (last) {
                    val text = buffer.toString()
                    buffer.clear()
                    try {
                        val status = json.decodeFromString<VoteStatus>(text)
                        trySend(status)
                    } catch (_: Exception) {
                    }
                }
                return super.onText(webSocket, data, last)
            }

            override fun onClose(webSocket: WebSocket, statusCode: Int, reason: String?): CompletionStage<*>? {
                launch {
                    delay(2.seconds)
                    connect()
                }
                return super.onClose(webSocket, statusCode, reason)
            }

            override fun onError(webSocket: WebSocket, error: Throwable?) {
                launch {
                    delay(2.seconds)
                    connect()
                }
            }
        }).exceptionally {
            launch {
                delay(2.seconds)
                connect()
            }
            null
        }
    }

    connect()

    awaitClose { }
}
