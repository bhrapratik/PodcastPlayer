package com.pratik.podcastplayer.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object NetworkClient {
    private const val BASE_URL = "https://itunes.apple.com"

    val httpClient: HttpClient by lazy {
        HttpClient {
            expectSuccess = true

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("KTOR: $message")
                    }
                }
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        explicitNulls = false
                    },
                    contentType = ContentType.Application.Json
                )

                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        explicitNulls = false
                    },
                    contentType = ContentType("text", "javascript")
                )
            }
        }
    }

    fun baseUrl(): String = BASE_URL
}
